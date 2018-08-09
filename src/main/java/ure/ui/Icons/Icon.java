package ure.ui.Icons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UConfig;

import javax.inject.Inject;

/**
 * A glyph plus colors defining a static entity representation for UI purposes.
 *
 */
public class Icon implements Cloneable {

    @Inject
    @JsonIgnore
    public URenderer renderer;
    @Inject
    @JsonIgnore
    public UConfig config;

    public UColor bgColor;
    public UColor fgColor;
    public char glyph;

    String name;
    int[] glyphVariants;

    UColor[] fgVariants;
    UColor[] bgVariants;

    protected String TYPE = "";

    boolean glow;

    public Icon() {
        Injector.getAppComponent().inject(this);
    }

    public Icon(String type) {
        this();
        this.TYPE= type;
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
            renderer.drawTile(glyph, x, y, fgColor);
        }
    }

    public Icon makeClone() {
        try {
            Icon clone = (Icon)clone();
            return clone;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Pick our variants, etc.
     */
    public void initialize() {

    }

    public UColor getBgColor() { return bgColor; }
    public void setBgColor(UColor bgColor) { this.bgColor = bgColor; }
    public UColor getFgColor() { return fgColor; }
    public void setFgColor(UColor fgColor) { this.fgColor = fgColor; }
    public char getGlyph() { return glyph; }
    public void setGlyph(char glyph) { this.glyph = glyph; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int[] getGlyphVariants() { return glyphVariants; }
    public void setGlyphVariants(int[] glyphVariants) { this.glyphVariants = glyphVariants; }
    public UColor[] getFgVariants() { return fgVariants; }
    public void setFgVariants(UColor[] fgVariants) { this.fgVariants = fgVariants; }
    public UColor[] getBgVariants() { return bgVariants; }
    public void setBgVariants(UColor[] bgVariants) { this.bgVariants = bgVariants; }
    public String getTYPE() { return TYPE; }
    public void setTYPE(String TYPE) { this.TYPE = TYPE; }
    public boolean isGlow() { return glow; }
    public void setGlow(boolean glow) { this.glow = glow; }
}
