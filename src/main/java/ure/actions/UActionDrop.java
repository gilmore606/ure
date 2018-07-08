package ure.actions;

import ure.things.UContainer;
import ure.actors.UActor;
import ure.things.UThing;

/**
 * Actor drops thing from her inventory, into her cell or a container.
 *
 */
public class UActionDrop extends UAction {

    public static String id = "DROP";

    UThing thing;
    UContainer destination;

    public UActionDrop(UActor theactor, UThing thething) {
        actor = theactor;
        thing = thething;
        destination = null;
    }
    public UActionDrop(UActor theactor, UThing thething, UContainer thedest) {
        actor = theactor;
        thing = thething;
        destination = thedest;
    }

    @Override
    void doFor(UActor actor) {
        if (thing != null) {
            if (destination == null) {
                destination = actor.location();
            }
            if (destination.willAcceptThing(thing)) {
                thing.moveTo(destination);
            }
        }
    }
}
