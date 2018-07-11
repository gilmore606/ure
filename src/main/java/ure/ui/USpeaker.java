package ure.ui;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.ALC_REFRESH;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

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
        long device = ALC10.alcOpenDevice((ByteBuffer)null);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);
        contextAttribList.put(ALC_REFRESH);
        contextAttribList.put(60);
        contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
        contextAttribList.put(2);

        contextAttribList.put(0);
        contextAttribList.flip();

        long newContext = ALC10.alcCreateContext(device, contextAttribList);
        if (!ALC10.alcMakeContextCurrent(newContext)) {
            throw new Exception("ALC10 failed to make audio context!");
        }
        AL.createCapabilities(deviceCaps);
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

    }
}
