package ure.things;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actions.Interactable;
import ure.actors.UActor;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.math.UColor;
import ure.render.URenderer;
import ure.ui.Icon;
import ure.ui.UCamera;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A real instance of a thing.
 *
 */
public abstract class ThingI implements UThing, UContainer, Entity, Interactable, Cloneable {

    @Inject
    @JsonIgnore
    public UCommander commander;

    protected String name;
    protected String iname;
    protected String dname;
    protected String plural;
    protected String type;
    protected char glyph;
    protected String description = "A thing.";
    protected int weight;
    protected boolean movable = true;
    protected int value;
    protected int[] color;
    protected int[] colorvariance = new int[]{0,0,0};
    protected String getFailMsg = "You can't pick that up.";
    protected String category = "misc";
    protected HashMap<String,Integer> stats;
    public String[] tags;
    public int[] spawnlevels;
    public String[] spawnterrain;


    public static final String TYPE = "";

    protected UColor glyphColor;
    protected boolean glyphOutline = false;
    protected Icon icon;

    @JsonIgnore
    protected UContainer location;  // What container am I in?  TODO: Reconnect after deserialization

    protected UCollection contents; // What's inside me?

    public ThingI() {
        Injector.getAppComponent().inject(this);
    }

    public void initialize() {
        setContents(new UCollection(this));
        if (getGlyphColor() == null && getColor() != null) {
            SetupColors();
        }
        setIcon(new Icon(getGlyph(), getGlyphColor(), null));
        stats = new HashMap<>();
    }

    public void SetupColors() {
        Random random = new Random();
        int[] thecolor = new int[]{color[0], color[1], color[2]};
        for (int i=0;i<3;i++) {
            if (colorvariance[i] > 0)
                thecolor[i] += (random.nextInt(colorvariance[i]) - colorvariance[i]/2);
        }
        setGlyphColor(new UColor(thecolor[0],thecolor[1],thecolor[2]));
    }

    public void setDisplayFields(String thename, char theglyph, UColor thecolor, boolean addOutline) {
        setName(thename);
        setGlyph(theglyph);
        setGlyphColor(thecolor);
        setGlyphOutline(addOutline);
    }

    public int glyphOffsetX() { return 0; }
    public int glyphOffsetY() { return 0; }

    public UColor getGlyphColor() {
        return glyphColor;
    }

    public Icon icon() { return getIcon(); }

    public String[] UIdetails(String context) {
        return new String[]{
                "Weight " + Integer.toString(getWeight()),
                "Value " + Integer.toString(getValue())
        };
    }

    public boolean drawGlyphOutline() {
        if (isGlyphOutline())
            return true;
        if (commander.config.isOutlineThings())
            return true;
        return false;
    }

    public void moveToCell(int x, int y) {
        moveToCell(area(), x, y);
    }
    public void moveToCell(UArea area, int x, int y) {
        UCell destination = area.cellAt(x,y);
        if (destination != null) {
            moveTo(destination);
            area.addedThing((UThing) this, x, y);
        }
    }

    public void moveTo(UContainer container) {
        leaveCurrentLocation();
        container.addThing(this);
        this.setLocation(container);
        this.contents.notifyMove();
    }

    public void leaveCurrentLocation() {
        if (getLocation() != null) {
            getLocation().removeThing(this);
        }
        this.setLocation(null);
    }

    public void addThing(UThing thing) {
        getContents().add(thing);
    }
    public void removeThing(UThing thing) {
        getContents().remove(thing);
    }
    public Iterator<UThing> iterator() {
        return getContents().iterator();
    }

    public int containerType() { return UContainer.TYPE_THING; }
    public boolean willAcceptThing(UThing thing) {
        return false;
    }

    public int areaX() { return getLocation().areaX(); }
    public int areaY() { return getLocation().areaY(); }

    public int cameraX(UCamera camera) {
        return getLocation().areaX() - camera.leftEdge;
    }
    public int cameraY(UCamera camera) {
        return getLocation().areaY() - camera.topEdge;
    }
    public UArea area() {
        if (getLocation() != null)
            return getLocation().area();
        return null;
    }

    public UThing makeClone() {
        try {
            return (UThing) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(" Cloning not allowed. ");
            return this;
        }
    }

    public boolean tryGetBy(UActor actor) {
        if (!isMovable()) {
            if (actor instanceof UPlayer)
                commander.printScroll(getGetFailMsg());
            return false;
        }
        return true;
    }

    public void gotBy(UActor actor) {
        if (getMsg(actor) != null)
            commander.printScroll(this.getMsg(actor));
    }
    public void droppedBy(UActor actor) {

    }

    public boolean isInteractable(UActor actor) {
        return false;
    }
    public float interactionFrom(UActor actor) {
        return 0f;
    }

    public String getMsg(UActor actor) {
        return getDescription();
    }
    public String walkMsg(UActor actor) { return "You see " + getIname() + "."; }
    public String getCategory() { return category; }
    public void setCategory(String _category) { category = _category; }

    //The camera class will call this, and tell where in screen coords to draw it.
    // TODO: Things should probably not be tied directly to the rendering system.  Ideally they would just be part of the data layer, not the presentation layer
    public void render(URenderer renderer, int x, int y, UColor light, float vis){
        int xoff = glyphOffsetX();
        int yoff = glyphOffsetY();
        char icon = getGlyph();
        UColor color = new UColor(this.getGlyphColor());
        if (this.drawGlyphOutline()) {
            renderer.drawGlyphOutline(icon, x, y, UColor.COLOR_BLACK, xoff, yoff);
        }
        color.illuminateWith(light, vis);
        renderer.drawGlyph(icon, x, y, color, xoff, yoff);
    }

    public void emote(String text) {
        commander.printScrollIfSeen(this, text);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIname() {
        if (iname != null && !iname.isEmpty())
            return iname;
        return "a " + getName();
    }

    public void setIname(String iname) {
        this.iname = iname;
    }

    public String getDname() {
        if (dname != null && !dname.isEmpty())
            return dname;
        return "the " + getName();
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDnamec() {
        return "The " + getName();
    }

    public String getPlural() {
        if (plural != null && !plural.isEmpty())
            return plural;
        char last = getName().charAt(getName().length()-1);
        if (last == 's')
            return getName() + "es";
        return getName() + "s";
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public char getGlyph() {
        return glyph;
    }

    public void setGlyph(char glyph) {
        this.glyph = glyph;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public int[] getColorvariance() {
        return colorvariance;
    }

    public void setColorvariance(int[] colorvariance) {
        this.colorvariance = colorvariance;
    }

    public String getGetFailMsg() {
        return getFailMsg;
    }

    public void setGetFailMsg(String getFailMsg) {
        this.getFailMsg = getFailMsg;
    }

    public void setGlyphColor(UColor glyphColor) {
        this.glyphColor = glyphColor;
    }

    public boolean isGlyphOutline() {
        return glyphOutline;
    }

    public void setGlyphOutline(boolean glyphOutline) {
        this.glyphOutline = glyphOutline;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public UContainer getLocation() {
        return location;
    }

    public void setLocation(UContainer location) {
        this.location = location;
    }

    public UCollection getContents() {
        return contents;
    }

    public void setContents(UCollection contents) {
        this.contents = contents;
    }

    public int getStat(String stat) {
        if (stats.containsKey(stat))
            return (int)(stats.get(stat));
        return 0;
    }
    public void setStat(String stat, int value) {
        stats.put(stat, value);
    }


    public boolean isTagAndLevel(String tag, int level) {
        if (spawnlevels == null) return false;
        if (level >= spawnlevels[0] && level <= spawnlevels[1]) {
            for (String test : tags) {
                if (test.equals(tag)) return true;
            }
        }
        return false;
    }

    public boolean canSpawnOnTerrain(String terrain) {
        if (spawnterrain == null) {
            return true;
        }
        for (String t : spawnterrain) {
            if (t.equals(terrain))
                return true;
        }
        return false;
    }

    public boolean isUsable(UActor actor) {
        return false;
    }

    public float useFrom(UActor actor) {
        return 0f;
    }

    public void notifyMove() {

    }
}
