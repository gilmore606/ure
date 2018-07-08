package ure.actions;

import ure.actors.UActor;
import ure.things.UThing;

/**
 * Actor tries to pick up the thing, from the ground or a container.
 *
 */
public class UActionGet extends UAction {

    public static String id = "GET";

    public static String nothingToGetMsg = "There's nothing here to get.";

    UThing thing;

    public UActionGet() {
        thing = null;
    }

    public UActionGet(UThing thething) {
        thing = thething;
    }

    @Override
    void doFor(UActor actor) {
        if (thing == null) {
            thing = actor.myCell().topThingAt();
            if (thing == null) {
                if (actor.isPlayer())
                    actor.camera.area.commander().printScroll(nothingToGetMsg);
            }
        }
        actor.tryGetThing(thing);
    }
}
