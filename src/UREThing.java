import java.awt.*;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A real instance of a thing.
 *
 */
public class UREThing implements UContainer {
    public String name;
    public char icon;

    public Color iconColor;
    public boolean iconOutline = false;

    UContainer location;  // What collection am I in?
    UCollection contents; // What's inside me?

    public UREThing(String thename, char theicon, Color thecolor, boolean addOutline) {
        name = thename;
        icon = theicon;
        iconColor = thecolor;
        iconOutline = addOutline;
        contents = new UCollection(this);
        location = null;
    }

    public void moveToCell(UREArea area, int x, int y) {
        leaveCurrentLocation();
        area.addThing(this, x, y);
    }

    public void moveToContainer(UContainer container) {
        leaveCurrentLocation();
        container.addThing(this);
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
}
