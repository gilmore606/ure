package ure.actors.actions;

import ure.things.UContainer;
import ure.actors.UActor;
import ure.things.UThing;

/**
 * Actor drops thing from her inventory, into her cell or a container.
 *
 */
public class ActionDrop extends UAction {

    public static String id = "DROP";

    UThing thing;
    UContainer destination;

    public ActionDrop(UActor theactor, UThing thething) {
        actor = theactor;
        thing = thething;
        destination = actor.getLocation();
    }
    public ActionDrop(UActor theactor, UThing thething, UContainer thedest) {
        actor = theactor;
        thing = thething;
        destination = thedest;
    }

    @Override
    void doMe() {
        if (thing != null) {
            if (destination == null) {
                destination = actor.getLocation();
            }
            if (!actor.tryDropThing(thing, destination))
                suppressEvent();
        }
    }
}
