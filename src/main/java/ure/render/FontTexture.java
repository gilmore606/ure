package ure.render;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * A class for reading, decoding, and loading the texture we use for writing text.
 */
public class FontTexture {

    // These numbers are somewhat arbitrary and assume a traditional ASCII kinda font.
    // These may need to be adjusted for the target font, or ideally we would compute
    // them somehow.
    public int bitmapWidth = 1024;
    public int bitmapHeight = 512;
    public int numberOfGlyphs = 96;
    public int ascent;
    public int descent;
    public int lineGap;
    public float fontSize;

    public STBTTBakedChar.Buffer glyphData = STBTTBakedChar.malloc(numberOfGlyphs);
    public int texId;

    // These will be filled with the most recently looked up glyph's width and height, in pixels
    public float[] glyphWidth = new float[1];
    public float[] glyphHeight = new float[1];

    public SolidColorData solidColorData = new SolidColorData();

    // This buffer holds the most recently looked up aligned quad.
    private STBTTAlignedQuad.Buffer alignedQuad = STBTTAlignedQuad.malloc(1);

    private ByteBuffer readFile(String filename) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(filename);
        byte[] bytes = IOUtils.toByteArray(is);
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length + 1);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }

    public void loadFromTTF(String resourcePath, float size) {
        fontSize = size;
        ByteBuffer ttfData;
        try {
            ttfData = readFile(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("Can't load font resource at path " + resourcePath, e);
        }
        readFontInfo(ttfData);
        ByteBuffer fontBitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
        STBTruetype.stbtt_BakeFontBitmap(ttfData, size, fontBitmap, bitmapWidth, bitmapHeight, 32, glyphData);
        // Add a white pixel at the bottom right corner of our bitmap to use as the texture for drawRect
        fontBitmap.put(bitmapWidth * bitmapHeight - 4, (byte)0xFF);
        fontBitmap.put(bitmapWidth * bitmapHeight - 3, (byte)0xFF);
        fontBitmap.put(bitmapWidth * bitmapHeight - 2, (byte)0xFF);
        fontBitmap.put(bitmapWidth * bitmapHeight - 1, (byte)0xFF);
        // can free the ttfData now
        texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, fontBitmap);
        // can free fontBitmap now
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glBindTexture(0, GL_TEXTURE_2D);
        System.out.println("*** Loaded font " + resourcePath);
    }

    private void readFontInfo(ByteBuffer ttfData) {
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, ttfData)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);

            float scaleFactor = stbtt_ScaleForPixelHeight(fontInfo, fontSize);
            ascent *= scaleFactor;
            descent *= scaleFactor;
            lineGap *= scaleFactor;
        }
    }

    public STBTTAlignedQuad glyphInfo(char c) {
        glyphWidth[0] = 0;
        glyphHeight[0] = 0;
        STBTruetype.stbtt_GetBakedQuad(glyphData, bitmapWidth, bitmapHeight, c - 32, glyphWidth, glyphHeight, alignedQuad.get(0), true);
        return alignedQuad.get(0);
    }

    public class SolidColorData {
        float u, v, uw, vh;

        public SolidColorData() {
            u = (float)(bitmapWidth - 1) / (float)bitmapWidth;
            v = (float)(bitmapHeight - 1) / (float)bitmapHeight;
            uw = 1f - u;
            vh = 1f - v;
        }
    }
}
