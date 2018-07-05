package ure.render;


import ure.UColor;
import ure.ui.View;

public interface URERenderer {

    interface KeyListener {
        void keyPressed(char key);
    }

    void setRootView(View root);
    void initialize();
    boolean windowShouldClose();
    void pollEvents();
    void setKeyListener(KeyListener listener);
    void render();

    // These will go away one we've worked out font rendering, views, etc.
    int glyphWidth();
    int glyphHeight();

    // Drawing primitives that the renderer will abstract
    void drawString(int x, int y, UColor col, String str);
    void drawGlyph(char glyph, int destx, int desty, UColor tint, int offX, int offY);
    void drawGlyphOutline(char glyph, int destx, int desty, UColor tint, int offX, int offY);
    void drawRect(int x, int y, int w, int h, UColor col);
    void drawRectBorder(int x, int y, int w, int h, int borderThickness, UColor bgColor, UColor borderColor);
}
