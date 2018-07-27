package ure.actors.actions;

import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.things.UThing;

/**
 * Actor tries to pick up the thing, from the ground or a container.
 *
 */
public class ActionGet extends UAction {

    public static String id = "GET";

    public static String nothingToGetMsg = "There's nothing here to get.";

    UThing thing;

    public ActionGet(UActor theactor) {
        super(theactor);
        thing = null;
    }

    public ActionGet(UActor theactor, UThing thething) {
        actor = theactor;
        thing = thething;
    }

    @Override
    void doMe() {
        if (thing == null) {
            thing = actor.myCell().topThingAt();
            if (thing == null) {
                if (actor instanceof UPlayer) {
                    commander.printScroll(nothingToGetMsg);
                }
            }
        }
        if (!actor.tryGetThing(thing))
            suppressEvent();
    }
}
