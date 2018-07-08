package ure;

import ure.actors.UActor;
import ure.things.UThing;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A bunch of Things in a place.
 *
 */

public class UCollection {

    private UContainer container;
    public ArrayList<UThing> things;
    ArrayList<UActor> actors;

    public UCollection(UContainer cont) {
        container = cont;
        things = new ArrayList<UThing>();
        actors = new ArrayList<UActor>();
    }

    public void remove(UThing thing) {
        actors.remove(thing);
        things.remove(thing);
    }

    public void add(UThing thing) {
        if (thing.isActor())
            actors.add((UActor)thing);
        else
            things.add(thing);
    }

    public Iterator<UThing> iterator() {
        return things.iterator();
    }

    public boolean hasThings() {
        return !things.isEmpty();
    }

    public boolean hasActors() {
        return !actors.isEmpty();
    }

    public UThing topThing() {
        if (things.isEmpty())
            return null;
        return things.get(0);
    }

    public UActor actor() {
        if (actors.isEmpty())
            return null;
        return actors.get(0);
    }
}
