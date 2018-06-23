import java.awt.*;
import java.util.Iterator;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A real instance of a thing.
 *
 */
public class UREThing implements UContainer {
    public String name;
    public char icon;

    Color iconColor;
    boolean drawIconOutline = false;

    UContainer location;  // What container am I in?
    UCollection contents; // What's inside me?

    public UREThing(String thename, char theicon, Color thecolor, boolean addOutline) {
        name = thename;
        icon = theicon;
        iconColor = thecolor;
        drawIconOutline = addOutline;
        contents = new UCollection(this);
        location = null;
    }

    public char getIcon() {
        return icon;
    }
    public Color getIconColor() {
        return iconColor;
    }
    public boolean drawIconOutline() {
        return drawIconOutline;
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
