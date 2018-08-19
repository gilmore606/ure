package ure.ui.sounds;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.io.IOUtils;

import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.math.UPath;
import ure.sys.UConfig;
import ure.sys.events.PlayerChangedAreaEvent;
import ure.sys.Injector;
import ure.sys.UAnimator;
import ure.sys.UCommander;

import javax.inject.Inject;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import ure.sys.events.TimeTickEvent;
import ure.terrain.UTerrain;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.alSource3i;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 * A singleton to play background music and sound effects
 *
 */
public class USpeaker implements UAnimator, Runnable {

    @Inject
    public UConfig config;
    @Inject
    public EventBus bus;

    Thread playerThread;
    UCommander commander;

    boolean initialized = false;
    boolean initializedFX = false;

    private long device;
    private long context;
    private ALCCapabilities alcCapabilities;
    private ALCapabilities alCapabilities;
    int efxMajor,efxMinor,maxAuxSends;
    int[] auxSends, effects;
    int filter;

    Deck BGMdeck1;
    Deck BGMdeck2;
    String BGMqueued;

    HashMap<String,Ambient> ambientSounds;
    ArrayList<Integer[]> activeSounds;
    ArrayList<Integer[]> activeSoundsTmp;


    /**
     * Info about an ambient sound we're playing.
     * TODO: write a better algo than 'nearest cell' for god's sake
     */
    public class Ambient {
        public String terrain;
        public int x, y;
        public int pointcount;
        public String file;
        boolean playing;
        float gain;
        IntBuffer source;
        IntBuffer buffer;

        public Ambient(UTerrain terrain) {
            this.terrain = terrain.getName();
            this.x = 0;
            this.y = 0;
            this.file = terrain.getAmbientsound();
            this.pointcount = 0;
            this.gain = terrain.getAmbientsoundGain();
            source = makePlaySource(gain);
            buffer = makePlayBuffer("sounds/" + file);
        }
        public void newFrame() {
            pointcount = 0;
        }
        public void addPoint(int pointx, int pointy, int range) {
            if (pointcount == 0) {
                x = pointx;
                y = pointy;
                pointcount++;
                return;
            }
            pointcount++;
            if (UPath.mdist(0,0,pointx,pointy) < UPath.mdist(0,0,x,y)) {
                x = pointx;
                y = pointy;
            }
        }
        public int x() {
            return x;
        }
        public int y() {
            return y;
        }
        public void updateState(UPlayer player) {
            if (playing && pointcount == 0)
                stop();
            else if (!playing && pointcount > 0)
                start();
            else {
                alSource3f(source.get(0), AL_POSITION, (float)x, 0f, (float)y);
                alSourcef(source.get(0), AL_GAIN, gain * config.getVolumeAmbient());
                addEnvironment(source.get(0), x, y);
            }
        }
        void stop() {
            playing = false;
            alSourceStop(source.get(0));
        }
        void start() {
            playing = true;
            alSourcei(source.get(0), AL_LOOPING, AL_TRUE);
            alSource3f(source.get(0), AL_POSITION, (float)x, (float)y, 0f);
            alSourcei(source.get(0), AL_BUFFER, buffer.get(0));
            alSourcef(source.get(0), AL_GAIN, gain * config.getVolumeAmbient());
            addEnvironment(source.get(0), x, y);
            alSourcePlay(source.get(0));
        }
    }


    /**
     * A virtual DJ deck for mixing BGM audio
     */
    public class Deck {
        public String filename;
        VorbisTrack track;
        AtomicInteger sampleIndex;
        public float gain;
        public int fadeDir;
        public USpeaker speaker;
        public Deck otherDeck;
        String deckID;

        private static final int BUFFER_SIZE = 1024*8;
        int format;
        int source;
        IntBuffer buffers;
        ShortBuffer pcm;
        long bufferOffset;
        long offset;
        long lastOffset;

        private float fadePerFrame;

        public Deck(USpeaker s, String id) {
            speaker = s;
            deckID = id;
            fadePerFrame = 1f / (config.getMusicFadeTime() * (1000 / config.getAnimFrameMilliseconds()));
        }
        public void linkOtherDeck(Deck d) { otherDeck = d; }

        public void animationTick() {
            if (fadeDir != 0) {
                gain += fadeDir * fadePerFrame;
                alSourcef(source, AL_GAIN, gain * config.getVolumeMusic());
                if (fadeDir == 1 && gain >= 1f) {
                    fadeInEnd();
                } else if (fadeDir == -1 && gain <= 0f) {
                    fadeOutEnd();
                }
            }
        }
        public void fadeInEnd() {
            fadeDir = 0;
        }
        public void fadeOutEnd() {
            fadeDir = 0;
            stop();
            String queued = speaker.getBGMqueued();
            if (queued != null) {
                fadeIn(queued);
                speaker.clearBGMqueued();
                otherDeck.fadeOut();
            }
        }
        public void fadeIn(String filename) {
            fadeDir = 1;
            gain = 0f;
            this.filename = filename;
            start();
            otherDeck.fadeOut();
        }
        public void fadeOut() {
            if (filename != null) {
                fadeDir = -1;
            }
        }
        public void start() {
            if (!initialized) return;
            System.out.println("SPEAKER: buffering playback for bgm '" + filename + "' on deck " + deckID);
            sampleIndex = new AtomicInteger();
            track = new VorbisTrack(config.getResourcePath() + filename, sampleIndex);
            this.format = track.channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
            this.pcm = memAllocShort(BUFFER_SIZE);
            alcSetThreadContext(context);
            IntBuffer b = makePlaySource(0f);
            source = b.get(0);
            //alSourcei(source, AL_DIRECT_CHANNELS_SOFT, AL_TRUE);
            buffers = memAllocInt(2);
            alGenBuffers(buffers);
            for (int i=0;i<buffers.limit();i++) {
                if (stream(buffers.get(i))==0) {
                    System.out.println("SPEAKER: ERROR - failed to buffer " + filename);
                    return;
                }
            }
            alSourceQueueBuffers(source,buffers);
            alSourcePlay(source);
            System.out.println("SPEAKER: playback started for bgm '" + filename + "' on deck " + deckID);
        }
        public void stop() {
            System.out.println("SPEAKER: ending playback for bgm '" + filename + "' on deck " + deckID);
            alSourceStop(source);
            alDeleteSources(source);
            alDeleteBuffers(buffers);
            memFree(buffers);
            memFree(pcm);
            track.close();
            filename = null;
            track = null;
            gain = 0f;
            fadeDir = 0;
        }
        int stream(int buffer) {
            int samples = 0;
            while (samples < BUFFER_SIZE) {
                pcm.position(samples);
                int samplesPerChannel = track.getSamples(pcm);
                if (samplesPerChannel == 0) {
                    break;
                }
                samples += samplesPerChannel * track.channels;
            }
            if (samples != 0) {
                pcm.position(0);
                pcm.limit(samples);
                alBufferData(buffer, format, pcm, track.sampleRate);
                pcm.limit(BUFFER_SIZE);
            }
            return samples;
        }
        public void update() {
            int processed = alGetSourcei(source, AL_BUFFERS_PROCESSED);
            for (int i=0;i<processed;i++) {
                bufferOffset += BUFFER_SIZE / track.channels;
                int buffer = alSourceUnqueueBuffers(source);
                if (stream(buffer) == 0) {
                    track.rewind();
                    lastOffset = offset = bufferOffset = 0;
                }
                alSourceQueueBuffers(source,buffer);
            }
            if (processed == 2) {
                alSourcePlay(source);
            }
        }
    }


    public USpeaker() {
        Injector.getAppComponent().inject(this);
        bus.register(this);

        BGMdeck1 = new Deck(this, "1");
        BGMdeck2 = new Deck(this, "2");
        BGMdeck1.linkOtherDeck(BGMdeck2);
        BGMdeck2.linkOtherDeck(BGMdeck1);

        ambientSounds = new HashMap<>();
        activeSounds = new ArrayList<>();
        activeSoundsTmp = new ArrayList<>();
    }

    /**
     * Grab the default audio device and set up to play it.
     *
     * @throws Exception
     */
    public void initialize() {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        device = alcOpenDevice(defaultDeviceName);
        int[] attributes = {0};
        context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);
        alcCapabilities = ALC.createCapabilities(device);
        alCapabilities = AL.createCapabilities(alcCapabilities);
        if (!alCapabilities.OpenAL10) {
            System.out.println("SPEAKER WARNING: OpenAL10 audio not supported on this system, giving up on sound");
            return;
        }
        System.out.println("SPEAKER OpenAL version: " + alGetString(AL_VERSION));
        efxMajor = alcGetInteger(device, ALC_EFX_MAJOR_VERSION);
        efxMinor = alcGetInteger(device, ALC_EFX_MINOR_VERSION);
        System.out.println("SPEAKER EFX version: " + Integer.toString(efxMajor) + "." + Integer.toString(efxMinor));
        maxAuxSends = alcGetInteger(device, ALC_MAX_AUXILIARY_SENDS);
        initialized = true;

        filter = alGenFilters();
        if (alGetError() != AL_NO_ERROR) System.out.println("SPEAKER WARNING: filter not supported");
        else {
            alFilteri(filter,AL_FILTER_TYPE,AL_FILTER_LOWPASS);
            alFilterf(filter, AL_LOWPASS_GAIN, 0.5f);
            alFilterf(filter, AL_LOWPASS_GAINHF, 0.5f);
            System.out.println("SPEAKER created lowpass occlusion filter");
        }

        if (maxAuxSends >= 2) {
            auxSends = new int[2];
            effects = new int[2];
            auxSends[0] = alGenAuxiliaryEffectSlots();
            auxSends[1] = alGenAuxiliaryEffectSlots();
            effects[0] = alGenEffects();
            effects[1] = alGenEffects();
            alEffecti(effects[0], AL_EFFECT_TYPE, AL_EFFECT_REVERB);
            alEffectf(effects[0], AL_REVERB_DECAY_TIME, 1.0f);
            alAuxiliaryEffectSloti(auxSends[0], AL_EFFECTSLOT_EFFECT, effects[0]);
            System.out.println("SPEAKER initialized reverb on AUX0");
            alEffecti(effects[1], AL_EFFECT_TYPE, AL_EFFECT_ECHO);
            alEffectf(effects[1], AL_ECHO_DELAY, 0.5f);
            alEffectf(effects[1], AL_ECHO_DAMPING, 0.5f);
            alEffectf(effects[1], AL_ECHO_FEEDBACK, 0.4f);
            alAuxiliaryEffectSloti(auxSends[1], AL_EFFECTSLOT_EFFECT, effects[1]);
            System.out.println("SPEAKER initialized echo on AUX1");
            initializedFX = true;
        } else {
            System.out.println("SPEAKER WARNING not enough aux sends -- disabling EFX");
        }
    }

    /**
     * TODO: read WAV files!
     * TODO: buffer and stream long ogg BGMs, this is simply terrible
     *
     * @param filename
     * @return
     */
    IntBuffer makePlayBuffer(String filename) {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        alGenBuffers(buffer);
        if (filename.endsWith(".ogg")) {
            stackPush();
            IntBuffer channelsBuffer = stackMallocInt(1);
            stackPush();
            IntBuffer sampleRateBuffer = stackMallocInt(1);
            ShortBuffer rawBuffer = stb_vorbis_decode_filename(config.getResourcePath() + filename, channelsBuffer, sampleRateBuffer);
            int channels = channelsBuffer.get();
            int sampleRate = sampleRateBuffer.get();
            stackPop();
            stackPop();

            int format = -1;
            if (channels == 1) format = AL_FORMAT_MONO16;
            else if (channels == 2) format = AL_FORMAT_STEREO16;

            alBufferData(buffer.get(0), format, rawBuffer, sampleRate);
            free(rawBuffer);
            return buffer;
        } else if (filename.endsWith(".wav")) {
            AudioInputStream stream = null;
            try {
                stream = AudioSystem.getAudioInputStream(new File(config.getResourcePath() + filename));
            } catch (Exception e) {
                e.printStackTrace();
            }
            AudioFormat format = stream.getFormat();
            if (format.isBigEndian()) System.out.println("SPEAKER: ***ERROR*** file '" + filename + "' : big-endian wav format not yet supported :(");
            int openALFormat = -1;
            switch(format.getChannels()) {
                case 1:
                    switch(format.getSampleSizeInBits()) {
                        case 8: openALFormat = AL_FORMAT_MONO8; break;
                        case 16: openALFormat = AL_FORMAT_MONO16; break;
                    } break;
                case 2:
                    switch(format.getSampleSizeInBits()) {
                        case 8: openALFormat = AL_FORMAT_STEREO8; break;
                        case 16: openALFormat = AL_FORMAT_STEREO16; break;
                    } break;
            }
            byte[] b = null;
            try {
                b = IOUtils.toByteArray(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteBuffer data = createByteBuffer(b.length).put(b);
            data.flip();
            alBufferData(buffer.get(0), openALFormat, data, (int)format.getSampleRate());
            return buffer;
        }
        return null;
    }


    IntBuffer makePlaySource(float gain) {
        IntBuffer source = BufferUtils.createIntBuffer(1);
        alGenSources(source);
        alSourcef(source.get(0), AL_GAIN, gain);
        alSourcef(source.get(0), AL_REFERENCE_DISTANCE, 2.0f);
        alSourcef(source.get(0), AL_MAX_DISTANCE, (float)config.getVolumeFalloffDistance());
        return source;
    }

    void addEnvironment(int source, int x, int y) {
        //if (!UPath.canSee(commander.player().areaX(),commander.player().areaY(), x,y,commander.player().area(),commander.player())) {
        //alSourcei(source, AL_DIRECT_FILTER, filter);
        //if (alGetError() != AL_NO_ERROR) System.out.println("SPEAKER: filter apply error");
        //} else {
        //alSourcei(source, AL_DIRECT_FILTER, AL_FILTER_NULL);
    //}

    }

    /**
     * Ask the BGM DJ to fade into a new music filename.
     */
    public void playBGM(String filename) {
        if (filename == null) return;
        if (!initialized) return;
        if (BGMdeck1.filename != null)
            if (BGMdeck1.filename.equals(filename) && (BGMdeck2.filename == null)) return;
        if (BGMdeck2.filename != null)
            if (BGMdeck2.filename.equals(filename) && (BGMdeck1.filename == null)) return;
        if (BGMdeck1.filename == null)
            BGMdeck1.fadeIn(filename);
        else if (BGMdeck2.filename == null)
            BGMdeck2.fadeIn(filename);
        else
            BGMqueued = filename;
    }

    public String getBGMqueued() { return BGMqueued; }
    public void clearBGMqueued() { BGMqueued = null; }

    @Subscribe
    public void playerChangedArea(PlayerChangedAreaEvent event) {
        if (commander.player() != null)
            findTerrainAmbients((UPlayer)commander.player());
        playBGM(event.destArea.getBackgroundMusic());
    }

    /**
     * We use the animation ticks to crossfade, and to clean up completed oneshot sounds.
     */
    public void animationTick() {
        BGMdeck1.animationTick();
        BGMdeck2.animationTick();
        activeSoundsTmp = (ArrayList<Integer[]>)activeSounds.clone();
        for (Integer[] sound : activeSoundsTmp) {
            int state = alGetSourcei(sound[0], AL_SOURCE_STATE);
            if (state == AL_STOPPED) {
                alDeleteSources(sound[0]);
                alDeleteBuffers(sound[1]);
                activeSounds.remove(sound);
            }
        }
    }

    public void playUI(Sound sound) {
        play(sound, config.getVolumeUI(), 0, 0, false);
    }
    public void playWorld(Sound sound, int x, int y) {
        play(sound, config.getVolumeWorld(), x - commander.player().areaX(), y - commander.player().areaY(), true);
    }
    void play(Sound sound, float prefgain, int x, int y, boolean useEnvironment) {
        play(sound.file(), sound.gain() * prefgain * config.getVolumeMaster(), x, y, useEnvironment);
    }

    void play(String file, float gain, int x, int y, boolean useEnvironment) {
        if (!initialized) return;
        int buffer = makePlayBuffer(file).get(0);
        int source = makePlaySource(gain).get(0);
        if (useEnvironment)
            addEnvironment(source, x, y);
        alSourcei(source, AL_LOOPING, AL_FALSE);
        alSource3f(source, AL_POSITION, (float)x, (float)y, 0f);
        alSource3f(source, AL_VELOCITY, 0f, 0f, 0f);
        alSourcei(source, AL_BUFFER, buffer);
        alSourcePlay(source);
        activeSounds.add(new Integer[]{source,buffer});
    }

    public void startThread(UCommander c) {
        commander = c;
        if (playerThread == null) {
            playerThread = new Thread(this);
            playerThread.start();
        } else if (!playerThread.isAlive()) {
            playerThread = new Thread(this);
            playerThread.start();
        }
    }

    public void run() {
        System.out.println("SPEAKER: background thread starting");
        alcSetThreadContext(context);
        while (!commander.isQuitGame()) {
            BGMdeck1.update();
            BGMdeck2.update();
            try {
                Thread.sleep(33);
            } catch (Exception e) { e.printStackTrace(); }
        }
        System.out.println("SPEAKER: game quit detected, background thread exiting");
        IntBuffer auxEffectSlotsBuf = (IntBuffer)BufferUtils.createIntBuffer(auxSends.length).put(auxSends).rewind();
        alDeleteAuxiliaryEffectSlots(auxEffectSlotsBuf);
        IntBuffer effectsBuf = (IntBuffer)BufferUtils.createIntBuffer(effects.length).put(effects).rewind();
        alDeleteEffects(effectsBuf);
        alDeleteFilters(filter);
        alcSetThreadContext(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);
        System.out.println("SPEAKER: OpenAL device closed.  Will I dream?");
    }

    @Subscribe
    public void hearTimeTick(TimeTickEvent e) {
        if (commander.player() != null)
            findTerrainAmbients((UPlayer)commander.player());
    }

    public void resetAmbients() {
        for (String name : ambientSounds.keySet()) {
            ambientSounds.get(name).newFrame();
            ambientSounds.get(name).updateState(null);
        }
    }
    public void findTerrainAmbients(UPlayer player) {
        int px = player.areaX();
        int py = player.areaY();
        UArea area = player.area();
        int range = player.getHearingrange();
        for (String name : ambientSounds.keySet()) {
            ambientSounds.get(name).newFrame();
        }
        for (int x=-range;x<range;x++) {
            for (int y=-range;y<range;y++) {
                UTerrain t = area.terrainAt(px+x,py+y);
                if (t != null) {
                    if (t.getAmbientsound() != null) {
                        if (!ambientSounds.containsKey(t.getName()))
                            ambientSounds.put(t.getName(), new Ambient(t));
                        ambientSounds.get(t.getName()).addPoint(x, y, range);
                    }
                }
            }
        }
        for (String name : ambientSounds.keySet()) {
            Ambient a = ambientSounds.get(name);
            a.updateState(player);
            System.out.println("SPEAKER: ambient " + name + " at " + Integer.toString(a.x()) + "," + Integer.toString(a.y()));
        }
    }
}
