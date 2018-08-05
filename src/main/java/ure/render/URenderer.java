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
    void drawGlyph(char glyph, int destx, int desty, UColor tint, int offX, int offY);
    void drawGlyphOutline(char glyph, int destx, int desty, UColor tint, int offX, int offY);
    void drawRect(int x, int y, int w, int h, UColor col);
    void drawRectBorder(int x, int y, int w, int h, int borderThickness, UColor bgColor, UColor borderColor);
    int getMousePosX();
    int getMousePosY();
    boolean getMouseButton();
}
