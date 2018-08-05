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

    public String getName() { return name; }
    public void setName(String s) { name = s; }
    public ArrayList<Bodypart> getParts() { return parts; }
    public void setParts(ArrayList<Bodypart> p) { parts = p; }
}
