package ure.ui;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.stb.STBVorbis.*;
import org.lwjgl.system.MemoryStack;
import ure.sys.Injector;
import ure.sys.UAnimator;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 * A singleton to play background music and sound effects
 *
 */
public class USpeaker implements UAnimator {

    @Inject
    UCommander commander;

    boolean initialized = false;

    private long context;
    private ALCCapabilities alcCapabilities;
    private ALCapabilities alCapabilities;

    Deck BGMdeck1;
    Deck BGMdeck2;
    String BGMqueued;

    /**
     * A virtual DJ deck for mixing BGM audio
     */
    public class Deck {
        public String track;
        public float gain;
        public int fadeDir;
        public USpeaker speaker;
        public Deck otherDeck;
        int source;
        int buffer;
        String deckID;

        public Deck(USpeaker s, String id) {
            speaker = s;
            deckID = id;
        }
        public void linkOtherDeck(Deck d) { otherDeck = d; }

        public void animationTick() {
            if (fadeDir != 0) {
                float fadePerFrame = commander.config.getMusicFadeTime() / (1000 / commander.config.getAnimFrameMilliseconds());
                gain += fadeDir * fadePerFrame;
                alSourcef(source, AL_GAIN, gain * commander.config.getVolumeMusic());
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
            track = filename;
            start();
            otherDeck.fadeOut();
        }
        public void fadeOut() {
            if (track != null) {
                fadeDir = -1;
            }
        }
        public void start() {
            System.out.println("SPEAKER: starting playback for bgm '" + track + "' on deck " + deckID);
            IntBuffer b = makePlaySource(0f);
            source = b.get(0);
            b = makePlayBuffer(track);
            buffer = b.get(0);
            alSourcei(source, AL_BUFFER, buffer);
            alSourcePlay(source);
        }
        public void stop() {
            alSourceStop(source);
            alDeleteSources(source);
            alDeleteBuffers(buffer);
            track = null;
            gain = 0f;
            fadeDir = 0;
        }
    }


    public USpeaker() {
        Injector.getAppComponent().inject(this);
        BGMdeck1 = new Deck(this, "1");
        BGMdeck2 = new Deck(this, "2");
        BGMdeck1.linkOtherDeck(BGMdeck2);
        BGMdeck2.linkOtherDeck(BGMdeck1);
    }

    /**
     * Grab the default audio device and set up to play it.
     *
     * @throws Exception
     */
    public void initialize() {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long device = alcOpenDevice(defaultDeviceName);
        int[] attributes = {0};
        context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);
        alcCapabilities = ALC.createCapabilities(device);
        alCapabilities = AL.createCapabilities(alcCapabilities);
        if (!alCapabilities.OpenAL10) {
            System.out.println("WARNING: OpenAL10 audio not supported on this system, giving up on sound");
            return;
        }
        initialized = true;
    }

    IntBuffer makePlayBuffer(String filename) {
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawBuffer = stb_vorbis_decode_filename(commander.config.getResourcePath() + filename, channelsBuffer, sampleRateBuffer);
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        stackPop();
        stackPop();

        int format = -1;
        if (channels == 1) format = AL_FORMAT_MONO16;
        else if (channels == 2) format = AL_FORMAT_STEREO16;

        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        alGenBuffers(buffer);
        alBufferData(buffer.get(0), format, rawBuffer,sampleRate);
        free(rawBuffer);
        return buffer;
    }

    IntBuffer makePlaySource(float gain) {
        IntBuffer source = BufferUtils.createIntBuffer(1);
        alGenSources(source);
        alSourcef(source.get(0), AL_GAIN, gain);
        return source;
    }

    /**
     * Ask the BGM DJ to fade into a new music track.
     */
    public void playBGM(String filename) {
        if (BGMdeck1.track != null)
            if (BGMdeck1.track.equals(filename) && (BGMdeck2.track == null)) return;
        if (BGMdeck2.track != null)
            if (BGMdeck2.track.equals(filename) && (BGMdeck1.track == null)) return;
        if (BGMdeck1.track == null)
            BGMdeck1.fadeIn(filename);
        else if (BGMdeck2.track == null)
            BGMdeck2.fadeIn(filename);
        else
            BGMqueued = filename;
    }

    public String getBGMqueued() { return BGMqueued; }
    public void clearBGMqueued() { BGMqueued = null; }

    /**
     * Play a sound with no particular source position.
     *
     * @param filename
     */
    public void play(String filename) {
        playAt(filename, 0f, 0f);
    }

    /**
     * Play a sound at a given location, transposed to audio space.
     *
     * @param filename
     * @param x -1.0f to 1.0f representing left-right position
     * @param y -1.0f to 1.0f representing north-south position
     */
    public void playAt(String filename, float x, float y) {

    }

    /**
     * We use the animation ticks to crossfade etc.
     */
    public void animationTick() {
        BGMdeck1.animationTick();
        BGMdeck2.animationTick();
    }

}
