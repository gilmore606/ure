import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;

public class URERenderer {
    private int fontSize = 16;
    private int outlineWidth = 2;
    private int fontPadX = 4;
    private int fontPadY = 2;
    private int cellPadX = 1;
    private int cellPadY = 2;
    private String fontName = "Courier New";
    private boolean smoothGlyphs = true;

    private HashMap<Character,BufferedImage> glyphCache;
    private HashMap<Character,BufferedImage> outlineCache;
    private Font font;

    public URERenderer() {
        glyphCache = new HashMap<Character,BufferedImage>();
        outlineCache = new HashMap<Character,BufferedImage>();
    }

    public int getCellWidth() {
        return fontSize+cellPadX;
    }
    public int getCellHeight() {
        return fontSize+cellPadY;
    }

    public void renderCamera(URECamera camera) {
        int cellw = getCellWidth();
        int cellh = getCellHeight();
        int camw = camera.getWidthInCells();
        int camh = camera.getHeightInCells();
        font = new Font(fontName, Font.BOLD, fontSize);
        Graphics g = camera.getGraphics();
        BufferedImage cameraImage = camera.getImage();
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,camw*cellw, camh*cellh);
        for (int x=0;x<camw;x++) {
            for (int y=0;y<camh;y++) {
                renderCell(camera, x, y, cellw, cellh, g, cameraImage);
            }
        }
    }

    void renderCell(URECamera camera, int x, int y, int cellw, int cellh, Graphics g, BufferedImage image) {
        float vis = camera.visibilityAt(x,y);
        URETerrain t = camera.terrainAt(x,y);
        if (t != null) {
            g.setColor(DimColor(t.bgColor, vis));
            g.fillRect(x*cellw, y*cellh, cellw, cellh);
            BufferedImage tGlyph = charToGlyph(t.icon, font);
            stampGlyph(tGlyph, image, x*cellw, y*cellh, DimColor(t.fgColor, vis));
        }
        Iterator<UREThing> things = camera.thingsAt(x,y);
        if (things != null) {
            while (things.hasNext()) {
                UREThing thing = things.next();
                System.out.println("  drawing a " + thing.name);
                char icon = thing.getIcon();
                Color color = thing.getIconColor();
                if (thing.drawIconOutline())
                    stampGlyph(charToOutline(icon, font), image, x * cellw, y * cellh, Color.BLACK);
                stampGlyph(charToGlyph(icon, font), image, x * cellw, y * cellh, color);
            }
        }
    }

    Color DimColor(Color color, float brightness) {
        if (brightness == 1.0f)
            return color;
        if (brightness < 0.01f)
            return Color.BLACK;
        //
        // THIS IS BROKEN
        //
        int r = (color.getRed() & 0xff0000) >> 16;
        int g = (color.getGreen() & 0xff00) >> 8;
        int b = (color.getBlue() & 0xff);
        r = (int)((float)(r) * brightness);
        g = (int)((float)(g) * brightness);
        b = (int)((float)(b) * brightness);
        r = (r & 0xff0000) >> 16;
        g = (g & 0xff00) >> 8;
        b = (b & 0xff);
        return new Color(r,g,b);
    }

    public void stampGlyph(BufferedImage srcImage, BufferedImage dstImage, int destx, int desty, Color tint) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int[] srcLine = new int[width];
        int[] dstLine = new int[width];
        float[] srcHSB = new float[3];

        // TODO: getRGB() all at once on the whole source image and dest rect

        for (int y=0;y<height;y++) {
            srcLine = srcImage.getRGB(0, y, width, 1, srcLine, 0, width);
            dstLine = dstImage.getRGB(destx, desty+y, width, 1, dstLine, 0, width);
            for (int x=0;x<width;x++) {
                if ((destx + x < dstImage.getWidth()) && (desty + y < dstImage.getHeight()) && (destx + x >= 0) && (desty + y >= 0)) {
                    int srcRGB = srcLine[x];
                    int dstRGB = dstLine[x];
                    int r1 = (srcRGB & 0xff0000) >> 16;
                    int g1 = (srcRGB & 0xff00) >> 8;
                    int b1 = (srcRGB & 0xff);
                    srcHSB = Color.RGBtoHSB(r1, g1, b1, srcHSB);
                    float alpha1 = srcHSB[2];
                    r1 = (int) ((float) tint.getRed() * alpha1);
                    g1 = (int) ((float) tint.getGreen() * alpha1);
                    b1 = (int) ((float) tint.getBlue() * alpha1);
                    int r2 = (dstRGB & 0xff0000) >> 16;
                    int g2 = (dstRGB & 0xff00) >> 8;
                    int b2 = (dstRGB & 0xff);
                    float alpha2 = 1.0f - alpha1;
                    int r3 = r1 + (int) ((float) r2 * alpha2);
                    int g3 = g1 + (int) ((float) g2 * alpha2);
                    int b3 = b1 + (int) ((float) b2 * alpha2);
                    int finalRGB = ((r3&0x0ff)<<16)|((g3&0x0ff)<<8)|(b3&0x0ff);
                    dstLine[x] = finalRGB;
                }
            }
            dstImage.setRGB(destx, desty + y, width, 1, dstLine, 0, width);
        }
    }

    BufferedImage charToGlyph(char c, Font font) {
        if (glyphCache.containsKey((Character)c)) {
            return glyphCache.get((Character)c);
        }
        int cellw = getCellWidth();
        int cellh = getCellHeight();
        BufferedImage glyph = new BufferedImage(cellw, cellh, BufferedImage.TYPE_INT_RGB);
        Graphics g = glyph.getGraphics();
        if (smoothGlyphs) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        }
        g.setColor(Color.BLACK);
        g.fillRect(0,0,cellw,cellh);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString(Character.toString(c), fontPadX, fontSize - fontPadY);
        glyphCache.put((Character)c, glyph);
        System.out.println("cached a glyph for " + Character.toString(c));
        return glyph;
    }

    BufferedImage charToOutline(char c, Font font) {
        if (outlineCache.containsKey((Character)c)) {
            return outlineCache.get((Character)c);
        }
        BufferedImage src = charToGlyph(c, font);
        int cellw = src.getWidth();
        int cellh = src.getHeight();
        BufferedImage outline = new BufferedImage(cellw, cellh, BufferedImage.TYPE_INT_RGB);
        Graphics g = outline.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,cellw,cellh);
        for (int y=0;y<cellh;y++) {
            for (int x=0;x<cellw;x++) {
                int pixel = src.getRGB(x,y);
                for (int offy=-outlineWidth;offy<=outlineWidth;offy++) {
                    for (int offx=-outlineWidth;offx<=outlineWidth;offx++) {
                        int tx = x+offx;
                        int ty = y+offy;
                        if (tx >=0 && ty >=0 && tx < cellw && ty < cellh) {
                            int destpixel = outline.getRGB(tx,ty);
                            if ((destpixel & 0xff) < (pixel & 0xff)) {
                                outline.setRGB(tx, ty, pixel);
                            }
                        }
                    }
                }
            }
        }
        outlineCache.put((Character)c, outline);
        System.out.println("cached a glyph outline for " + Character.toString(c));
        return outline;
    }
}
