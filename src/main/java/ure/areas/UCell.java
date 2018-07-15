package ure.areas;

import ure.actors.UPlayer;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actions.UAction;
import ure.things.UCollection;
import ure.things.UContainer;
import ure.actors.UActor;
import ure.terrain.UTerrain;
import ure.things.UThing;

import javax.inject.Inject;
import java.util.Iterator;

/**
 * UCell represents a single XY cell of an area.
 *
 * These are created on area creation along with a terrain.  They have no type and should
 * not need to be subclassed, however, a custom UCell class can be given to Area on creation.
 *
 * TODO: actually implement what I just said in Area
 */
public class UCell implements UContainer {

    @Inject
    UCommander commander;

    UArea area;
    public int x,y;
    UTerrain terrain;
    float sunBrightness;
    private UCollection contents;
    boolean isSeen = false;

    public UCell(UArea theArea, int thex, int they, UTerrain theTerrain) {
        Injector.getAppComponent().inject(this);
        contents = new UCollection(this);
        area = theArea;
        x = thex;
        y = they;
        useTerrain(theTerrain);
    }

    public float sunBrightness() {
        return terrain.getSunvis();
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
        if (actor instanceof UPlayer && contents.hasThings()) {
            UThing thing = contents.topThing();
            commander.printScroll(thing.walkMsg(actor));
        }
        terrain.walkedOnBy(actor, this);
    }

    /**
     * URE assumes only one actor can be considered to be in a cell at one time, however,
     * we internally allow for a list of actors.
     *
     * @return
     */
    public UActor actorAt() {
        if (contents.hasActors())
            return contents.actor();
        return null;
    }

    /**
     * Get the thing 'on top' of the pile of things here.
     *
     * @return
     */
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

    /**
     * Does anything in the cell prevent this action from being done?
     *
     * @param action
     * @return
     */
    public boolean preventAction(UAction action) {
        return false;
    }
    /**
     * Is there any thing here named this?
     *
     * @param thing
     * @return
     */
    public boolean hasA(String thing) {
        for (UThing t : contents.getThings()) {
            if (t.getName().equals(thing))
                return true;
        }
        return false;
    }
    public int areaX() { return x; }
    public int areaY() { return y; }
    public UArea area() { return area; }

    /**
     * Take a terrain object and make it our terrain.
     *
     * @param t
     */
    public void useTerrain(UTerrain t) {
        terrain = t;
        t.becomeReal(this);
    }

    public void animationTick() {
        UActor actor = actorAt();
        if (actor != null)
            actor.animationTick();
        terrain.animationTick();
    }
}
