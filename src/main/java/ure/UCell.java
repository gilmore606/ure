package ure;

import ure.actors.UActor;
import ure.terrain.UTerrain;
import ure.things.UThing;

import java.util.Iterator;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A single xy grid cell of an Area
 *
 */
public class UCell implements UContainer {
    UArea area;
    public int x,y;
    UTerrain terrain;
    float sunBrightness;
    private UCollection contents;
    boolean isSeen = false;

    public UCell(UArea theArea, int thex, int they, UTerrain theTerrain) {
        contents = new UCollection(this);
        area = theArea;
        x = thex;
        y = they;
        useTerrain(theTerrain);
    }

    public float sunBrightness() {
        return terrain.sunvis();
    }

    public void setSeen(boolean theseen) {
        isSeen = theseen;
    }
    public boolean isSeen() {
        return isSeen;
    }
    public UTerrain terrain() {
        return terrain;
    }

    public void addThing(UThing thing) {
        contents.add(thing);
    }
    public void removeThing(UThing thing) {
        contents.remove(thing);
        area.hearRemoveThing(thing);
    }
    public Iterator<UThing> iterator() {
        return contents.iterator();
    }
    public int containerType() { return UContainer.TYPE_CELL; }

    public void moveTriggerFrom(UActor actor) {
        if (actorAt() != null) {
            actorAt().moveTriggerFrom(actor);
        } else {
            terrain.moveTriggerFrom(actor, this);
        }
    }

    public void walkedOnBy(UActor actor) {
        if (actor.isPlayer() && contents.hasThings()) {
            UThing thing = contents.topThing();
            if (area.commander() != null)
                area.commander().printScroll(thing.walkMsg(actor));
        }
        terrain.walkedOnBy(actor, this);
    }

    public UActor actorAt() {
        if (contents.hasActors())
            return contents.actor();
        return null;
    }

    public UThing topThingAt() {
        return contents.topThing();
    }

    public boolean willAcceptThing(UThing thing) {
        if (terrain != null) {
            if (terrain.isPassable()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasA(String thing) {
        for (UThing t : contents.things) {
            if (t.name().equals(thing))
                return true;
        }
        return false;
    }
    public int areaX() { return x; }
    public int areaY() { return y; }
    public UArea area() { return area; }

    public void useTerrain(UTerrain t) {
        terrain = t;
        t.becomeReal(this);
    }

    public void animationTick() {
        terrain.animationTick();
    }
}
