package ure.things;

import ure.areas.UArea;
import ure.things.UThing;

import java.util.Iterator;

public interface UContainer {
    int TYPE_CELL = 1;
    int TYPE_THING = 2;

    int areaX();
    int areaY();
    UArea area();
    void addThing(UThing thing);
    void removeThing(UThing thing);
    Iterator<UThing> iterator();
    int containerType();
    boolean willAcceptThing(UThing thing);
}
