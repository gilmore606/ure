package ure.things;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actors.actions.Interactable;
import ure.actors.UActor;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.math.UColor;
import ure.render.URenderer;
import ure.ui.Icon;
import ure.ui.UCamera;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A real instance of a thing.
 *
 */
public abstract class UThing implements UContainer, Entity, Interactable, Cloneable {

    public static final String TYPE = "";

    @Inject
    @JsonIgnore
    public UCommander commander;

    protected String name;
    protected long ID;
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
    protected String[] tags;
    protected int[] spawnlevels;
    protected String[] spawnterrain;

    protected UColor glyphColor;
    protected boolean glyphOutline = false;
    protected Icon icon;

    @JsonIgnore
    protected UContainer location;  // What container am I in?

    protected UCollection contents; // What's inside me?

    @JsonIgnore
    protected boolean closed;

    public UThing() {
        Injector.getAppComponent().inject(this);
    }

    /**
     * Set up a new template object fresh from resource JSON deserializing, to make it cloneable.
     */
    public void initializeAsTemplate() {
        setContents(new UCollection(this, this.name));
        if (getGlyphColor() == null && getColor() != null) {
            SetupColors();
        }
        setIcon(new Icon(getGlyph(), getGlyphColor(), null));
        stats = new HashMap<>();
        contents = new UCollection();
    }

    /**
     * Set up a fresh clone from a template object.
     */
    public void initializeAsCloneFrom(UThing template) {
        stats = (HashMap)template.stats.clone();
        contents = template.contents.clone();
        contents.reconnect(null, this);
        location = null;
    }

    public long getID() { return ID; }
    public void setID(long newID) { ID = newID; }

    /**
     * This method is purely for making a unique string ID for debug logging.
     */
    public String NN() { return this.name + " (" + Long.toString(ID) + ")"; }

    public void reconnect(UArea area, UContainer container) {
        this.location = container;
        contents.reconnect(area, this);
    }

    public void closeOut() {
        icon = null;
        stats = null;
        contents.closeOut();
        contents = null;
        closed = true;
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

    public ArrayList<String> UIdetails(String context) {
        ArrayList<String> d = new ArrayList<>();
        d.add("Weight " + Integer.toString(getWeight()));
        d.add("Value " + Integer.toString(getValue()));
        return d;
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
            UThing clone = (UThing) super.clone();

            return clone;
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
    public void render(URenderer renderer, int x, int y, UColor light, float vis){
        char icon = getGlyph();
        UColor color = new UColor(this.getGlyphColor());
        if (this.drawGlyphOutline()) {
            renderer.drawGlyphOutline(icon, x + glyphOffsetX(), y + glyphOffsetY(), renderer.glyphWidth(), renderer.glyphHeight(), UColor.COLOR_BLACK);
        }
        color.illuminateWith(light, vis);
        renderer.drawGlyph(icon, x + glyphOffsetX(), y + glyphOffsetY(), renderer.glyphWidth(), renderer.glyphHeight(), color);
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
        int[] levels = getSpawnlevels();
        if (levels == null || levels.length < 2) return false;
        if (level >= levels[0] && level <= levels[1]) {
            for (String test : getTags()) {
                if (test.equals(tag)) return true;
            }
        }
        return false;
    }

    public boolean canSpawnOnTerrain(String terrain) {
        if (getSpawnterrain() == null) {
            return true;
        }
        for (String t : getSpawnterrain()) {
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


    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int[] getSpawnlevels() {
        return spawnlevels;
    }

    public void setSpawnlevels(int[] spawnlevels) {
        this.spawnlevels = spawnlevels;
    }

    public String[] getSpawnterrain() {
        return spawnterrain;
    }

    public void setSpawnterrain(String[] spawnterrain) {
        this.spawnterrain = spawnterrain;
    }

    public void animationTick() {

    }
}
