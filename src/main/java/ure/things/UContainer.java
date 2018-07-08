package ure.things;

import ure.areas.UArea;
import ure.things.UThing;

import java.util.Iterator;

public interface UContainer {
    public static int TYPE_CELL = 1;
    public static int TYPE_THING = 2;

    public int areaX();
    public int areaY();
    public UArea area();
    public void addThing(UThing thing);
    public void removeThing(UThing thing);
    public Iterator<UThing> iterator();
    public int containerType();
    public boolean willAcceptThing(UThing thing);
}
