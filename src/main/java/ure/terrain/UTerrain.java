package ure.terrain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.sys.Entity;
import ure.sys.Injector;
import ure.sys.UAnimator;
import ure.sys.UCommander;
import ure.actions.Interactable;
import ure.areas.UCell;
import ure.math.UColor;
import ure.actors.UActor;
import ure.ui.Icon;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * A real serializable instance of a terrain which exists in a single cell.  Subclass this to
 * create new subtypes of Terrain.  To extend base terrain, use TerrainDeco.
 *
 * For API reference when extending this class to create custom terrain types, see the method docs of
 * TerrainDeco.
 *
 */

public abstract class UTerrain implements Entity, Cloneable, UAnimator, Interactable {

    @Inject
    @JsonIgnore
    UCommander commander;

    @JsonIgnore
    protected UCell cell;

    public static final String TYPE = "";

    protected String name;
    protected long ID;
    protected String plural;
    protected String type;
    protected String walkmsg = "";
    protected String bonkmsg = "";
    protected char filechar;
    protected char glyph;
    protected Icon icon;
    protected String category;
    protected String variants;
    protected HashMap<String,Integer> stats = new HashMap<>();
    protected int[] fgcolor;
    protected int[][] fgvariants;
    protected int[] bgcolor;
    protected int[] bgvariance;
    protected int[][] bgvariants;

    protected UColor fgColor;
    protected UColor bgColor;

    protected UColor fgColorBuffer;
    protected UColor bgColorBuffer;

    protected boolean passable;
    protected boolean opaque;
    protected boolean spawnok;
    protected boolean breaklatch = false;
    protected boolean glow = false;
    protected float sunvis = 0.0f;
    protected float movespeed = 1.0f;

    protected int animationFrame;
    protected int animationFrames;


    public UTerrain() {
        Injector.getAppComponent().inject(this);
    }

    public void initialize() {
        setFgColor(new UColor(fgcolor[0], fgcolor[1], fgcolor[2]));
        setBgColor(new UColor(bgcolor[0], bgcolor[1], bgcolor[2]));
        setFgColorBuffer(new UColor(0f,0f,0f));
        setBgColorBuffer(new UColor(0f, 0f ,0f));
        setIcon(new Icon(getGlyph(), getFgColor(), getBgColor()));
    }

    public void reconnect(UArea area, UCell cell) {
        // TODO: Back reference
        this.cell = cell;
    }

    public void closeOut() {
        icon = null;
        stats = null;
    }

    public ArrayList<String> UIdetails(String context) { return null; }

    public void becomeReal(UCell c) {
        cell = c;
        initialize();
        if (getBgvariants() != null) {
            Random r = new Random();
            getBgColor().set(getBgvariants()[r.nextInt(getBgvariants().length - 1)]);
        }
        if (getFgvariants() != null) {
            Random r = new Random();
            getFgColor().set(getFgvariants()[r.nextInt(getFgvariants().length-1)]);
        }
        if (getBgvariance() != null) {
            Random r = new Random();
            getBgColor().set(getBgColor().iR() + r.nextInt(getBgvariance()[0]) - getBgvariance()[0] / 2,
                    getBgColor().iG() + r.nextInt(getBgvariance()[1]) - getBgvariance()[1] / 2,
                    getBgColor().iB() + r.nextInt(getBgvariance()[2]) - getBgvariance()[2] / 2);
        }
    }

    public char glyph(int x, int y) {
        if (getVariants() == null)
            return getGlyph();
        int seed = (x * y * 19 + 1883) / 74;
        int period = getVariants().length();
        return getVariants().charAt(seed % period);
    }

    public int glyphOffsetX() {
        return 0;
    }
    public int glyphOffsetY() {
        return 0;
    }

    public void moveTriggerFrom(UActor actor, UCell cell) {
        if (isPassable(actor)) {
            actor.moveToCell(cell.areaX(), cell.areaY());
        } else {
            actor.walkFail(cell);
        }
    }

    public boolean preventMoveFrom(UActor actor) {
        return false;
    }

    public void walkedOnBy(UActor actor, UCell cell) {
        if (actor instanceof UPlayer) {
            printScroll(getWalkmsg(), cell);
        }
    }

    public boolean isInteractable(UActor actor) {
        return false;
    }

    public float interactionFrom(UActor actor) {
        return 0f;
    }

    // TODO: Why does this method exist?
    public void printScroll(String msg, UCell cell) {
        if (getWalkmsg() != null)
            if (getWalkmsg().length() > 0)
                commander.printScroll(msg);
    }

    public UTerrain makeClone() {
        try {
            return (UTerrain) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(" Cloning not allowed. ");
            return this;
        }
    }

    public void animationTick() {
        if (getAnimationFrames() < 1) return;
        setAnimationFrame(getAnimationFrame() + 1);
        if (getAnimationFrame() >= getAnimationFrames())
            setAnimationFrame(0);
        //cell.area().redrawCell(cell.areaX(), cell.areaY());
    }

    public boolean isPassable() { return passable; }
    public boolean isPassable(UActor actor) { return isPassable(); }
    public boolean isOpaque() { return opaque; }
    public boolean isSpawnok() { return spawnok; }
    public String getName() { return name; }
    public String getPlural() { return plural != null ? plural : getName() + "s"; }
    public Icon getIcon() { return icon; }
    public char getGlyph() { return glyph; }
    public String getCategory() { return category; }

    public int getStat(String stat) {
        if (getStats().containsKey(stat))
            return (int)(getStats().get(stat));
        return 0;
    }

    public void setStat(String stat, int value) {
        getStats().put(stat, value);
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWalkmsg() {
        return walkmsg;
    }

    public void setWalkmsg(String walkmsg) {
        this.walkmsg = walkmsg;
    }

    public String getBonkmsg() {
        return bonkmsg;
    }

    public void setBonkmsg(String bonkmsg) {
        this.bonkmsg = bonkmsg;
    }

    public char getFilechar() {
        return filechar;
    }

    public void setFilechar(char filechar) {
        this.filechar = filechar;
    }

    public void setGlyph(char glyph) {
        this.glyph = glyph;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getVariants() {
        return variants;
    }

    public void setVariants(String variants) {
        this.variants = variants;
    }

    public HashMap<String, Integer> getStats() {
        return stats;
    }

    public void setStats(HashMap<String, Integer> stats) {
        this.stats = stats;
    }

    public int[] getFgcolor() {
        return fgcolor;
    }

    public void setFgcolor(int[] fgcolor) {
        this.fgcolor = fgcolor;
    }

    public int[][] getFgvariants() {
        return fgvariants;
    }

    public void setFgvariants(int[][] fgvariants) {
        this.fgvariants = fgvariants;
    }

    public int[] getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(int[] bgcolor) {
        this.bgcolor = bgcolor;
    }

    public int[] getBgvariance() {
        return bgvariance;
    }

    public void setBgvariance(int[] bgvariance) {
        this.bgvariance = bgvariance;
    }

    public int[][] getBgvariants() {
        return bgvariants;
    }

    public void setBgvariants(int[][] bgvariants) {
        this.bgvariants = bgvariants;
    }

    public UColor getFgColor() {
        return fgColor;
    }

    public void setFgColor(UColor fgColor) {
        this.fgColor = fgColor;
    }

    public UColor getBgColor() {
        return bgColor;
    }

    public void setBgColor(UColor bgColor) {
        this.bgColor = bgColor;
    }

    public UColor getFgColorBuffer() {
        return fgColorBuffer;
    }

    public void setFgColorBuffer(UColor fgColorBuffer) {
        this.fgColorBuffer = fgColorBuffer;
    }

    public UColor getBgColorBuffer() {
        return bgColorBuffer;
    }

    public void setBgColorBuffer(UColor bgColorBuffer) {
        this.bgColorBuffer = bgColorBuffer;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }

    public boolean isBreaklatch() {
        return breaklatch;
    }

    public void setBreaklatch(boolean breaklatch) {
        this.breaklatch = breaklatch;
    }

    public boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public float getSunvis() {
        return sunvis;
    }

    public void setSunvis(float sunvis) {
        this.sunvis = sunvis;
    }

    public float getMovespeed() {
        return movespeed;
    }

    public float getMoveSpeed(UActor actor) {
        return movespeed;
    }

    public void setMovespeed(float movespeed) {
        this.movespeed = movespeed;
    }

    public int getAnimationFrame() {
        return animationFrame;
    }

    public void setAnimationFrame(int animationFrame) {
        this.animationFrame = animationFrame;
    }

    public int getAnimationFrames() {
        return animationFrames;
    }

    public void setAnimationFrames(int animationFrames) {
        this.animationFrames = animationFrames;
    }

    public long getID() { return ID; }
    public void setID(long newID) { ID = newID; }
}
