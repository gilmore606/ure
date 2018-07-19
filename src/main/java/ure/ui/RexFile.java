package ure.ui;

import ure.math.UColor;
import ure.render.URenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class RexFile extends View {

    ArrayList<RexLayer> layers;
    int width;
    int height;

    public class RexLayer {
        int width;
        int height;
        char[][] glyphs;
        UColor[][] fgColors;
        UColor[][] bgColors;
        public RexLayer(int _w, int _h, char[][] _g, UColor[][] _fg, UColor[][] _bg) {
            width = _w;
            height = _h;
            glyphs = _g;
            fgColors = _fg;
            bgColors = _bg;
        }
    }

    public RexFile(String filename) {
        layers = new ArrayList<>();
        try {
            byte[] compressed = Files.readAllBytes(new File(filename).toPath());
            byte[] decompressed = gzipDecodeByteArray(compressed);
            ByteBuffer bb = ByteBuffer.wrap(decompressed);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int version = bb.getInt();
            int no_layers = bb.getInt();

            for (int i=0;i<no_layers;i++) {
                int width = bb.getInt();
                int height = bb.getInt();
                UColor[][] fgColors = new UColor[width][height];
                UColor[][] bgColors = new UColor[width][height];
                char[][] glyphs = new char[width][height];
                for (int index=0;index<(width*height);index++) {
                    int x = index / height;
                    int y = index % height;
                    char glyph = (char)bb.getInt();
                    int fR = (int) bb.get() & 0xff;
                    int fG = (int) bb.get() & 0xff;
                    int fB = (int) bb.get() & 0xff;
                    int bR = (int) bb.get() & 0xff;
                    int bG = (int) bb.get() & 0xff;
                    int bB = (int) bb.get() & 0xff;
                    fgColors[x][y] = new UColor((int)fR,(int)fG,(int)fB);
                    bgColors[x][y] = new UColor((int)bR,(int)bG,(int)bB);
                    glyphs[x][y] = glyph;
                }
                layers.add(new RexLayer(width,height,glyphs,fgColors,bgColors));
                if (width > this.width)
                    this.width = width;
                if (height > this.height)
                    this.height = height;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] gzipDecodeByteArray(byte[] data) {
        GZIPInputStream gzipInputStream = null;
        try {
            gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[1024];
            while (gzipInputStream.available() > 0) {
                int count = gzipInputStream.read(buffer, 0, 1024);
                if (count > 0) {
                    outputStream.write(buffer, 0, count);
                }
            }
            outputStream.close();
            gzipInputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void draw(URenderer renderer, float alpha) {
        int xpos = 0;
        int ypos = 0;
        int gw = renderer.glyphWidth();
        int gh = renderer.glyphHeight();
        for (RexLayer layer : layers) {
            for (int x = 0;x < width;x++) {
                for (int y = 0;y < height;y++) {
                    UColor bgColor = layer.bgColors[x][y];
                    bgColor.setAlpha(alpha);
                    renderer.drawRect(xpos + x * gw, ypos + y * gh, gw, gh, bgColor);
                    UColor fgColor = layer.fgColors[x][y];
                    fgColor.setAlpha(alpha);
                    renderer.drawGlyph((char)layer.glyphs[x][y], xpos+x*gw, ypos+y*gh, fgColor, 0, 0);
                }
            }
        }
    }
}
