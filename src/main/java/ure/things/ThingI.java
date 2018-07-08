package ure.things;

import ure.actors.UActor;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.math.UColor;
import ure.render.URenderer;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A real instance of a thing.
 *
 */
public abstract class ThingI implements UThing, UContainer, Cloneable {

    public String name;
    public String iname;
    public String dname;
    public String plural;
    public String type;
    public char glyph;
    public String description = "A thing.";
    public int weight;
    public boolean movable = true;
    public int value;
    public int[] color;
    public int[] colorvariance = new int[]{0,0,0};
    public String getFailMsg = "You can't pick that up.";

    public static final String TYPE = "";

    UColor glyphColor;
    public boolean glyphOutline = false;

    protected UContainer location;  // What container am I in?
    protected UCollection contents; // What's inside me?

    public static boolean isActor = false;

    public boolean isActor() {
        return false;
    }
    public boolean isPlayer() { return false; }

    public void initialize() {
        contents = new UCollection(this);
        if (glyphColor == null && color != null) {
            SetupColors();
        }
    }

    void SetupColors() {
        Random random = new Random();
        int[] thecolor = new int[]{color[0], color[1], color[2]};
        for (int i=0;i<3;i++) {
            if (colorvariance[i] > 0)
                thecolor[i] += (random.nextInt(colorvariance[i]) - colorvariance[i]/2);
        }
        glyphColor = new UColor(thecolor[0],thecolor[1],thecolor[2]);
    }

    public void setDisplayFields(String thename, char theglyph, UColor thecolor, boolean addOutline) {
        name = thename;
        glyph = theglyph;
        glyphColor = thecolor;
        glyphOutline = addOutline;
    }

    public String iname() {
        if (iname != null && iname != "")
            return iname;
        return "a " + name;
    }
    public String dname() {
        return "the " + name;
    }
    public String dnamec() {
        return "The " + name;
    }
    public String plural() {
        if (plural != null && plural != "")
            return plural;
        char last = name.charAt(name.length()-1);
        if (last == 's')
            return name + "es";
        return name + "s";
    }

    public char glyph() {
        return glyph;
    }
    public int glyphOffsetX() { return 0; }
    public int glyphOffsetY() { return 0; }

    public UColor getGlyphColor() {
        return glyphColor;
    }
    public boolean drawGlyphOutline() {
        return glyphOutline;
    }

    public UContainer location() { return location; }

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
        this.location = container;
    }

    public void leaveCurrentLocation() {
        if (location != null) {
            location.removeThing(this);
        }
        this.location = null;
    }

    public void addThing(UThing thing) {
        contents.add(thing);
    }
    public void removeThing(UThing thing) {
        contents.remove(thing);
    }
    public Iterator<UThing> iterator() {
        return contents.iterator();
    }
    public int containerType() { return UContainer.TYPE_THING; }
    public boolean willAcceptThing(UThing thing) {
        return false;
    }
    public int areaX() { return location.areaX(); }
    public int areaY() { return location.areaY(); }
    public UArea area() { return location.area(); }

    public UThing getClone() {
        try {
            return (UThing) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(" Cloning not allowed. ");
            return this;
        }
    }

    public boolean tryGetBy(UActor actor) {
        if (!movable) {
            if (actor.isPlayer())
                actor.commander().printScroll(getFailMsg);
            return false;
        }
        return true;
    }

    public void gotBy(UActor actor) {
        if (getMsg(actor) != null)
            area().commander().printScroll(this.getMsg(actor));
    }

    public String getMsg(UActor actor) {
        return description;
    }

    public String walkMsg(UActor actor) { return "You see " + iname() + "."; }

    //The camera class will call this, and tell where in screen coords to draw it.
    // TODO: Things should probably not be tied directly to the rendering system.  Ideally they would just be part of the data layer, not the presentation layer
    public void render(URenderer renderer, int x, int y, UColor light, float vis){
        char icon = this.glyph();
        UColor color = new UColor(this.getGlyphColor());
        if (this.drawGlyphOutline()) {
            renderer.drawGlyphOutline(icon, x, y, UColor.COLOR_BLACK, 0, 0);
        }
        color.illuminateWith(light, vis);
        //drawGlyph(charToGlyph(icon, font), image, x * cellw, y * cellh, color, 0, 0);
        renderer.drawGlyph(icon, x, y, color, 0, 0);
    }

    public void emote(String text) {
        area().commander().printScrollIfSeen(this, text);
    }

    public String name() { return name; }
}
