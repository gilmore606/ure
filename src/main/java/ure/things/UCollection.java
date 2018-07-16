package ure.things;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.UActor;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An inventory of things in a location (either a cell, or another thing or actor).
 *
 */

public class UCollection {

    @JsonIgnore
    private UContainer container; // TODO: Reconnect this after deserialization

    protected ArrayList<UThing> things;
    protected ArrayList<UActor> actors;

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
        if (thing instanceof UActor)
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

    /**
     * Whatever we're in just moved.
     */
    public void notifyMove() {
        for (UThing thing : things)
            thing.notifyMove();
    }

    public ArrayList<UThing> getThings() {
        return things;
    }

    public ArrayList<UActor> getActors() {
        return actors;
    }
}
