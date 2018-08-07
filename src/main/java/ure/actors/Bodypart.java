package ure.actors;

public class Bodypart {
    public String name;
    public int slots;
    public float size;

    public Bodypart() { }
    public Bodypart(String name, int slots, float size) {
        this.name = name;
        this.slots = slots;
        this.size = size;
    }
    public String equipUIstring() {
        if (name.equals("equip"))
            return "equip:";
        else
            return "on " + name + ":";
    }
    public String getName() { return name; }
    public void setName(String s) { name = s; }
    public int getSlots() { return slots; }
    public void setSlots(int i) { slots = i; }
    public float getSize() { return size; }
    public void setSize(float f) { size = f; }
}
