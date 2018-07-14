package ure.ui;

import ure.math.UColor;
import ure.render.URenderer;

/**
 * A glyph plus colors defining a static entity representation for UI purposes.
 *
 */
public class Icon {

    public UColor bgColor;
    public UColor fgColor;
    public char glyph;

    public Icon(char glyph, UColor fgColor, UColor bgColor) {
        this.glyph = glyph;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
    }

    public void draw(URenderer renderer, int x, int y) {
        if (bgColor != null) {
            renderer.drawRect(x,y,renderer.glyphWidth(),renderer.glyphHeight(),bgColor);
        }
        if (fgColor != null) {
            renderer.drawGlyph(glyph, x, y, fgColor, 0, 0);
        }
    }
}
