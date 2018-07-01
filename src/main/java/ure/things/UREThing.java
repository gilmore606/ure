package ure.things;

import ure.*;
import ure.actors.UREActor;

import java.util.Iterator;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A real instance of a thing.
 *
 */
public class UREThing implements UContainer, Cloneable {
    public String name;
    public String iname;
    public String dname;
    public String plural;
    public String type;
    public char glyph;
    public String description = "A thing.";
    public int weight;
    public int value;
    public int[] color;

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
        if (glyphColor == null && color != null)
            glyphColor = new UColor(color[0],color[1],color[2]);
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

    public char getGlyph() {
        return glyph;
    }
    public UColor getGlyphColor() {
        return glyphColor;
    }
    public boolean drawGlyphOutline() {
        return glyphOutline;
    }

    public void moveToCell(int x, int y) {
        moveToCell(area(), x, y);
    }
    public void moveToCell(UREArea area, int x, int y) {
        leaveCurrentLocation();
        this.location = area.addThing(this, x, y);
    }

    public void moveToContainer(UContainer container) {
        leaveCurrentLocation();
        container.addThing(this);
        this.location = container;
    }

    void leaveCurrentLocation() {
        if (location != null) {
            location.removeThing(this);
        }
        this.location = null;
    }

    public void addThing(UREThing thing) {
        contents.add(thing);
    }
    public void removeThing(UREThing thing) {
        contents.remove(thing);
    }
    public Iterator<UREThing> iterator() {
        return contents.iterator();
    }
    public int containerType() { return UContainer.TYPE_THING; }
    public boolean willAcceptThing(UREThing thing) {
        return false;
    }
    public int areaX() { return location.areaX(); }
    public int areaY() { return location.areaY(); }
    public UREArea area() { return location.area(); }

    public UREThing getClone() {
        try {
            return (UREThing) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(" Cloning not allowed. ");
            return this;
        }
    }

    public boolean tryGetBy(UREActor actor) {
        return true;
    }

    public void gotBy(UREActor actor) {
        if (this.getMsg(actor) != null)
            area().commander().printScroll(this.getMsg(actor));
    }

    public String getMsg(UREActor actor) {
        return description;
    }
    public String walkMsg(UREActor actor) { return "You see " + iname() + "."; }
}
