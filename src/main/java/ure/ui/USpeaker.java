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

    /**
     * TODO: BGM DJ
     *
     * two decks, can be playing a track or null
     * onrequest:
     *  both null : start fadein on one
     *  one playing: start fadeout on playing, start fadein on null
     *  both playing, no queue: put queue
     *  both playing, queue: replace queue w request
     * onfadeoutEnd:
     *  null deck
     *  if queue: start fadein on this, null queue, start fadeout on other
     *
     *
     */
    public USpeaker() {
        Injector.getAppComponent().inject(this);
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

        ShortBuffer rawBuffer = stb_vorbis_decode_filename(filename, channelsBuffer, sampleRateBuffer);
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

    public void playBGM(String filename) {
        IntBuffer bgmBuffer = makePlayBuffer(filename);
        IntBuffer bgmSource = makePlaySource(commander.config.getVolumeMusic());
        alSourcei(bgmSource.get(0), AL_BUFFER, bgmBuffer.get(0));
        alSourcePlay(bgmSource.get(0));
    }

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

    }

}
