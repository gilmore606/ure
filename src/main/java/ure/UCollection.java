package ure;

import ure.actors.UREActor;
import ure.things.UREThing;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A bunch of Things in a place.
 *
 */

public class UCollection {

    private UContainer container;
    ArrayList<UREThing> things;
    ArrayList<UREActor> actors;

    public UCollection(UContainer cont) {
        container = cont;
        things = new ArrayList<UREThing>();
        actors = new ArrayList<UREActor>();
    }

    public void remove(UREThing thing) {
        actors.remove(thing);
        things.remove(thing);
    }

    public void add(UREThing thing) {
        if (thing.isActor())
            actors.add((UREActor)thing);
        else
            things.add(thing);
    }

    public Iterator<UREThing> iterator() {
        return things.iterator();
    }

    public boolean hasThings() {
        return !things.isEmpty();
    }

    public boolean hasActors() {
        return !actors.isEmpty();
    }

    public UREThing topThing() {
        if (things.isEmpty())
            return null;
        return things.get(0);
    }

    public UREActor actor() {
        if (actors.isEmpty())
            return null;
        return actors.get(0);
    }
}
