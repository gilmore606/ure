package ure.render;


import ure.UColor;
import ure.URECamera;
import ure.URECommander;

public interface URERenderer {
    void initialize();
    boolean windowShouldClose();
    void pollEvents();
    void setCommander(URECommander commander); // TODO: This should be an event listener, not a direct reference to commander
    int cellWidth(); // TODO: Cell width should be determined at a higher level?
    int cellHeight(); // TODO: Cell height should be determined at a higher level?
    void drawCamera(URECamera camera);
    void drawString(int x, int y, UColor col, String str);
    void drawGlyph(char glyph, int destx, int desty, UColor tint, int offX, int offY);
    void drawGlyphOutline(char glyph, int destx, int desty, UColor tint, int offX, int offY);
    void render();
}
