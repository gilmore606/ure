package ure.ui.Icons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Entity;
import ure.sys.Injector;
import ure.sys.UCommander;
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
    public UCommander commander;
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
    String editorHelpMsg;

    boolean glow;
    int animOffset;
    float animAmpX, animAmpY, animFreq;


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
            renderer.drawTile(glyph(), x+glyphX(), y+glyphY(), fgColor);
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
        animOffset = random.nextInt(config.getFPStarget());
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

    /**
     * This method is unfortunately necessary to copy Icon properties to new subclass instances in GlyphEd.
     * If you add or modify properties of Icon, you need to ensure those properties are copied in this method.
     */
    public void copyFrom(Icon source) {
        bgColor = source.bgColor;
        fgColor = source.fgColor;
        glyph = source.glyph;
        name = source.name;
        glyphVariants = source.glyphVariants;
        fgVariants = source.fgVariants;
        bgVariants = source.bgVariants;
        glow = source.glow;
        animAmpX = source.animAmpX;
        animAmpY = source.animAmpY;
        animFreq = source.animFreq;
    }

    public int glyph() {
        return glyph(0);
    }
    int glyph(int frame) {
        return glyph;
    }
    int glyphX() {
        return 0;
    }
    int glyphY() {
        return 0;
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
    public float getAnimAmpX() { return animAmpX; }
    public void setAnimAmpX(float f) { animAmpX = f; }
    public float getAnimAmpY() { return animAmpY; }
    public void setAnimAmpY(float f) { animAmpY = f; }
    public float getAnimFreq() { return animFreq; }
    public void setAnimFreq(float f) { animFreq = f; }
    public String getEditorHelpMsg() { return editorHelpMsg; }
    public void setEditorHelpMsg(String s) { editorHelpMsg = s; }
}
