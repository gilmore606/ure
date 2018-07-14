package ure.ui;

import ure.math.UColor;

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
}
