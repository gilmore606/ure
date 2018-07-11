package ure.ui;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 * A singleton to play background music and sound effects
 *
 */
public class USpeaker {

    public USpeaker() {

    }

    /**
     * Grab the default audio device and set up to play it.
     *
     * @throws Exception
     */
    public void initialize() throws Exception {

    }

    /**
     * Play a sound with no particular source position.
     *
     * @param filename
     */
    public void play(String filename) {

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
     * Switch to a new background music track.
     * @param filename
     * @param xfade Seconds to fade out the current music and fade in the new.
     */
    public void switchBGM(String filename, int xfade) {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long device = alcOpenDevice(defaultDeviceName);
        int[] attributes = {0};
        long context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        if (!alCapabilities.OpenAL10) {
            System.out.println("WARNING: OpenAL10 audio not supported.  Going silent.");
            return;
        }

        int channels, sampleRate;
        ShortBuffer rawAudioBuffer;

        try (MemoryStack stack  = stackPush()) {
            IntBuffer channelsBuffer = stackMallocInt(1);
            IntBuffer sampleRateBuffer = stackMallocInt(1);
            rawAudioBuffer = stb_vorbis_decode_filename(filename, channelsBuffer, sampleRateBuffer);
            channels = channelsBuffer.get();
            sampleRate = sampleRateBuffer.get();
        }

        int format = -1;
        if (channels == 1)
            format = AL_FORMAT_MONO16;
        else if (channels == 2)
            format = AL_FORMAT_STEREO16;
        int bufferPointer = alGenBuffers();
        alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);
        free(rawAudioBuffer);

        int sourcePointer = alGenSources();
        alSourcei(sourcePointer, AL_BUFFER, bufferPointer);

        alSourcePlay(sourcePointer);
    }
}
