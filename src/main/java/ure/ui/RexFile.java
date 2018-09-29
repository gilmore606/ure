package ure.ui;

import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UConfig;

import javax.inject.Inject;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class RexFile extends View {

    @Inject
    UConfig config;

    ArrayList<RexLayer> layers;
    public int width;
    public int height;

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
        Injector.getAppComponent().inject(this);
        layers = new ArrayList<>();
        try {
            GZIPInputStream gunzipStream = new GZIPInputStream(getClass().getResourceAsStream(filename));
            byte[] decompressed = gunzipStream.readAllBytes();
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

    public void draw(URenderer renderer, float alpha) { draw(renderer, alpha, 0, 0); }
    public void draw(URenderer renderer, float alpha, int xpos, int ypos) {
        int gw = config.getTileWidth();
        int gh = config.getTileHeight();
        for (RexLayer layer : layers) {
            for (int x = 0;x < width;x++) {
                for (int y = 0;y < height;y++) {
                    UColor bgColor = layer.bgColors[x][y];
                    if (!(bgColor.iR() == 255 && bgColor.iG() == 0 && bgColor.iB() == 255)) { // 255,0,255 = transparent
                        bgColor.setAlpha(alpha);
                        renderer.drawRect(xpos + x * gw, ypos + y * gh, gw, gh, bgColor);

                        UColor fgColor = layer.fgColors[x][y];
                        fgColor.setAlpha(alpha);
                        renderer.drawTile((char) layer.glyphs[x][y], xpos + x * gw, ypos + y * gh, fgColor);
                    }
                }
            }
        }
    }
}
