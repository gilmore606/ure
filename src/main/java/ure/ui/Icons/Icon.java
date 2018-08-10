package ure.ui.Icons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UConfig;

import javax.inject.Inject;
import java.util.Random;

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
    @Inject
    @JsonIgnore
    public Random random;

    public UColor bgColor;
    public UColor fgColor;
    public int glyph;

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
        initializeFgColor();
        initializeBgColor();
        initializeGlyph();
    }

    public void initializeFgColor() {
        if (fgVariants != null) {
            int r = random.nextInt(fgVariants.length+1);
            if (r > 0)
                fgColor = fgVariants[r-1];
        }
    }
    public void initializeBgColor() {
        if (bgVariants != null) {
            int r = random.nextInt(bgVariants.length+1);
            if (r > 0)
                bgColor = bgVariants[r-1];
        }
    }
    public void initializeGlyph() {
        if (glyphVariants != null) {
            int r = random.nextInt(glyphVariants.length+1);
            if (r > 0)
                glyph = glyphVariants[r-1];
        }
    }

    public UColor getBgColor() { return bgColor; }
    public void setBgColor(UColor bgColor) { this.bgColor = bgColor; }
    public UColor getFgColor() { return fgColor; }
    public void setFgColor(UColor fgColor) { this.fgColor = fgColor; }
    public int getGlyph() { return glyph; }
    public void setGlyph(int glyph) { this.glyph = glyph; }
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
