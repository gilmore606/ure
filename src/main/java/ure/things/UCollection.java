package ure.things;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCell;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An inventory of things in a location (either a cell, or another thing or actor).
 *
 */

public class UCollection {

    @JsonIgnore
    private UContainer container;
    @JsonIgnore
    boolean closed;
    @JsonIgnore
    String debugtag;

    protected ArrayList<UThing> things;
    protected ArrayList<UActor> actors;

    public UCollection() {
        things = new ArrayList<>();
        actors = new ArrayList<>();
        closed = false;
    }

    public UCollection(UContainer cont, String debugtag) {
        things = new ArrayList<>();
        actors = new ArrayList<>();
        container = cont;
        this.debugtag = debugtag;
    }

    public void reconnect(UArea area, UContainer container) {
        this.container = container;
        for (UThing thing : things) {
            thing.reconnect(area, container);
        }
        for (UActor actor : actors) {
            actor.reconnect(area, container);
        }
    }

    public void closeOut() {
        if (container instanceof UPlayer)
            System.out.println("*** BUG : player's collection is closing!");
        if (things != null) {
            for (UThing thing : things) {
                thing.closeOut();
            }
        }
        if (actors != null) {
            for (UActor actor : actors) {
                actor.closeOut();
            }
        }
        things = null;
        actors = null;
        container = null;
        closed = true;
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
        if (things == null) {
            System.out.println("***BUG null things in collection.notifyMove() - i was made by: " + debugtag);
            if (closed)
                System.out.println("*** THIS COLLECTION IS CLOSED");
            System.out.println("*** container: " + container.toString());
        }
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
