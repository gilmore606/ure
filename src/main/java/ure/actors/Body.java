package ure.actors;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the list of body parts an actor has.
 */
public class Body {

    public String name;
    public ArrayList<Bodypart> parts;


    public Body() {

    }
    public Body(String name, ArrayList<Bodypart> parts) {
        this.name = name;
        this.parts = parts;
    }

    public boolean hasPart(String partname) { return hasPart(partname, 1); }
    public boolean hasPart(String partname, int slotcount) {
        for (Bodypart part : parts) {
            if (part.name.equals(partname))
                if (part.slots >= slotcount)
                    return true;
        }
        return false;
    }

    public int slotsForPart(String partname) {
        for (Bodypart part : parts) {
            if (part.name.equals(partname))
                return part.slots;
        }
        return 0;
    }

    public String getName() { return name; }
    public void setName(String s) { name = s; }
    public ArrayList<Bodypart> getParts() { return parts; }
    public void setParts(ArrayList<Bodypart> p) { parts = p; }
}
