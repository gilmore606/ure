package ure.render;


import ure.UColor;
import ure.URECamera;

public interface URERenderer {

    interface KeyListener {
        void keyPressed(char key);
    }

    void initialize();
    boolean windowShouldClose();
    void pollEvents();
    void setKeyListener(KeyListener listener);
    int cellWidth(); // TODO: Cell width should be determined at a higher level?
    int cellHeight(); // TODO: Cell height should be determined at a higher level?
    void drawCamera(URECamera camera);
    void drawString(int x, int y, UColor col, String str);
    void drawGlyph(char glyph, int destx, int desty, UColor tint, int offX, int offY);
    void drawGlyphOutline(char glyph, int destx, int desty, UColor tint, int offX, int offY);
    void drawRect(int x, int y, int w, int h, UColor col);
    void drawRectBorder(int x, int y, int w, int h, int borderThickness, UColor bgColor, UColor borderColor);
    void render();
}
