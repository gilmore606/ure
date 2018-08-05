package ure.actors;

import java.util.HashMap;

/**
 * Represents the list of body parts an actor has.
 */
public class Body {

    public String name;
    public HashMap<String,Bodypart> parts;

    public class Bodypart {
        public String name;
        public int slots;
        public float size;
        public Bodypart(String name, int slots, float size) {
            this.name = name;
            this.slots = slots;
            this.size = size;
        }
    }

    public Body(String name, HashMap<String,Bodypart> parts) {
        this.name = name;
        this.parts = parts;
    }

    public String getName() { return name; }
    public void setName(String s) { name = s; }
    public HashMap<String,Bodypart> getParts() { return parts; }
    public void setParts(HashMap<String,Bodypart> p) { parts = p; }
}
