package ure.ui.sounds;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_get_samples_short_interleaved;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A streamable OGG decoder.  Stolen in desperation from:
 * https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/stb/Vorbis.java
 *
 */
public class VorbisTrack implements AutoCloseable {
    private ByteBuffer encodedAudio;
    private final long handle;
    public final int channels;
    public final int sampleRate;
    final int samplesLength;
    final float samplesSec;
    private final AtomicInteger sampleIndex;

    private Log log = LogFactory.getLog(VorbisTrack.class);

    VorbisTrack(String filePath, AtomicInteger sampleIndex) {
        try {
            encodedAudio = ioResourceToByteBuffer(filePath, 256*1024);
        } catch (IOException e) { e.printStackTrace(); }

        try (MemoryStack stack = stackPush()) {
            IntBuffer error = stack.mallocInt(1);
            handle = stb_vorbis_open_memory(encodedAudio,error,null);
            if (handle == NULL)
                throw new RuntimeException("Failed to open OGG file.  Error: " + error.get(0));
            STBVorbisInfo info = STBVorbisInfo.mallocStack(stack);
            stb_vorbis_get_info(handle, info);
            this.channels = info.channels();
            this.sampleRate = info.sample_rate();
            log.debug("vorbis file detected " + Integer.toString(channels) + " channel " + Integer.toString(sampleRate) + " samplerate");
        }
        this.samplesLength = stb_vorbis_stream_length_in_samples(handle);
        this.samplesSec = stb_vorbis_stream_length_in_seconds(handle);
        this.sampleIndex = sampleIndex;
        sampleIndex.set(0);
    }

    public void close() {
        stb_vorbis_close(handle);
    }

    void progressBy(int samples) {
        sampleIndex.set(sampleIndex.get() + samples);
    }

    void rewind() { seek(0); }

    void seek(int sampleIndex) {
        stb_vorbis_seek(handle, sampleIndex);
        setSampleIndex(sampleIndex);
    }

    void setSampleIndex(int sampleIndex) {
        this.sampleIndex.set(sampleIndex);
    }

    synchronized int getSamples(ShortBuffer pcm) {
        return stb_vorbis_get_samples_short_interleaved(handle, channels, pcm);
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size()+1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                    InputStream source = VorbisTrack.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source);
            ) {
                buffer = createByteBuffer(bufferSize);
                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) { break; }
                    if (buffer.remaining() == 0) { buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); }
                }
            }
        }
        buffer.flip();
        return buffer.slice();
    }
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
