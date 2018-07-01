package ure;

import ure.actors.UREActor;
import ure.terrain.URETerrain;
import ure.things.UREThing;

import java.util.Iterator;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A single xy grid cell of an Area
 *
 */
public class UCell implements UContainer {
    UREArea area;
    int x,y;
    URETerrain terrain;
    float sunBrightness;
    public UCollection contents;
    boolean isSeen = false;

    public UCell(UREArea theArea, int thex, int they, URETerrain theTerrain) {
        contents = new UCollection(this);
        area = theArea;
        x = thex;
        y = they;
        useTerrain(theTerrain);
    }

    public float sunBrightness() {
        return terrain.sunvis;
    }

    public void setSeen(boolean theseen) {
        isSeen = theseen;
    }
    public boolean isSeen() {
        return isSeen;
    }
    public URETerrain terrain() {
        return terrain;
    }

    public void addThing(UREThing thing) {
        contents.add(thing);
    }
    public void removeThing(UREThing thing) {
        contents.remove(thing);
        area.hearRemoveThing(thing);
    }
    public Iterator<UREThing> iterator() {
        return contents.iterator();
    }
    public int containerType() { return UContainer.TYPE_CELL; }

    public void moveTriggerFrom(UREActor actor) {
        if (actorAt() != null) {
            actorAt().moveTriggerFrom(actor);
        } else {
            terrain.moveTriggerFrom(actor, this);
        }
    }

    public void walkedOnBy(UREActor actor) {
        if (actor.isPlayer() && contents.hasThings()) {
            UREThing thing = contents.topThing();
            if (area.commander() != null)
                area.commander().printScroll(thing.walkMsg(actor));
        }
        terrain.walkedOnBy(actor, this);
    }

    public UREActor actorAt() {
        if (contents.hasActors())
            return contents.actor();
        return null;
    }

    public UREThing topThingAt() {
        return contents.topThing();
    }

    public boolean willAcceptThing(UREThing thing) {
        if (terrain != null) {
            if (terrain.isPassable()) {
                return true;
            }
        }
        return false;
    }
    public int areaX() { return x; }
    public int areaY() { return y; }
    public UREArea area() { return area; }

    public void useTerrain(URETerrain t) {
        terrain = t;
        t.becomeReal(this);
    }

    public void animationTick() {
        if (terrain.animationFrames > 0)
            terrain.animationTick();
    }
}
