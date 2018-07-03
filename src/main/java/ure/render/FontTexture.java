package ure.render;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;

/**
 * A class for reading, decoding, and loading the texture we use for writing text.
 */
public class FontTexture {
    int width;
    int height;
    int channels;

    public int loadTexture(String filename) {
        ByteBuffer imageBuffer;
        try {
            imageBuffer = readFile(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 0);
        if(image == null){
            throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
        }

        width = w.get(0);
        height = h.get(0);
        channels = comp.get(0);

        int texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        if (channels == 3) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        glBindTexture(0, GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        return texId;
    }

    private ByteBuffer readFile(String filename) throws IOException{
        InputStream is = this.getClass().getResourceAsStream(filename);
        byte[] bytes = IOUtils.toByteArray(is);
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length + 1);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }

}
