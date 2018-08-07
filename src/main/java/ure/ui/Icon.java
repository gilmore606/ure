package ure.ui;

import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UConfig;

import javax.inject.Inject;

/**
 * A glyph plus colors defining a static entity representation for UI purposes.
 *
 */
public class Icon {

    @Inject
    URenderer renderer;
    @Inject
    UConfig config;

    public UColor bgColor;
    public UColor fgColor;
    public char glyph;

    public Icon() {
        Injector.getAppComponent().inject(this);
    }

    public Icon(char glyph, UColor fgColor, UColor bgColor) {
        this();
        this.glyph = glyph;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
    }

    public void draw(int x, int y) {
        if (bgColor != null) {
            renderer.drawRect(x,y,config.getTileWidth(),config.getTileHeight(),bgColor);
        }
        if (fgColor != null) {
            renderer.drawGlyph(glyph, x, y, fgColor);
        }
    }
}
