package ure;

import java.util.Iterator;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A real instance of a thing.
 *
 */
public class UREThing implements UContainer {
    public String name;
    public char glyph;

    UColor glyphColor;
    boolean glyphOutline = false;

    UContainer location;  // What container am I in?
    UCollection contents; // What's inside me?

    public UREThing(String thename, char theglyph, UColor thecolor, boolean addOutline) {
        name = thename;
        glyph = theglyph;
        glyphColor = thecolor;
        glyphOutline = addOutline;
        contents = new UCollection(this);
        location = null;
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
}
