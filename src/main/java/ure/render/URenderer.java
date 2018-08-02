package ure.render;


import ure.math.UColor;
import ure.sys.GLKey;
import ure.sys.Injector;
import ure.ui.View;

public interface URenderer {

    interface KeyListener {
        void keyPressed(GLKey key);
    }

    /**
     * Get the top level view for attaching overlays.
     *
     * @return
     */
    View getRootView();

    /**
     * Set the top level view for the game window.
     * @param root
     */

    void setRootView(View root);

    /**
     * Set up the rendering system.
     */
    void initialize();

    /**
     * A check to see whether the game window has been closed.  Test this in the game loop to see if the game should
     * terminate.
     * @return true if the window is closing.
     */
    boolean windowShouldClose();

    /**
     * Poll the game window for any key events.
     */
    void pollEvents();


    /**
     * Set a listener for key events from the game window.
     * @param listener
     */
    void setKeyListener(KeyListener listener);

    /**
     * Repaint the game window.
     */
    void render();

    // These will go away one we've worked out font rendering, views, etc.
    int glyphWidth();
    int glyphHeight();

    // Drawing primitives that the renderer will abstract
    void drawString(int x, int y, UColor col, String str);

    /**
     * Draw a glyph a the given x,y coordinates.
     * @param glyph
     * @param destx
     * @param desty
     * @param tint
     */
    void drawGlyph(char glyph, int destx, int desty, UColor tint);

    /**
     * Draw a glyph in the center of the box with its origin at destx,desty that is cellWidth pixels wide and
     * cellHeight pixels tall.  This is intended for use when drawing glyphs within a cell, so that the cell
     * size can be independent of the font size.
     * @param glyph
     * @param destx
     * @param desty
     * @param cellWidth
     * @param cellHeight
     * @param tint
     */
    void drawGlyph(char glyph, int destx, int desty, int cellWidth, int cellHeight, UColor tint);

    /**
     * Draw an outline for a particular glyph so that it stands out from its background.  This is intended for use
     * when drawing in cells, so it also takes the cellWidth and cellHeight into account to match the cell version
     * of {link #drawGlyph(char, int, int, int, int, UColor)}.
     * @param glyph
     * @param destx
     * @param desty
     * @param cellWidth
     * @param cellHeight
     * @param tint
     */
    void drawGlyphOutline(char glyph, int destx, int desty, int cellWidth, int cellHeight, UColor tint);
    void drawRect(int x, int y, int w, int h, UColor col);
    void drawRectBorder(int x, int y, int w, int h, int borderThickness, UColor bgColor, UColor borderColor);
    int getMousePosX();
    int getMousePosY();
}
