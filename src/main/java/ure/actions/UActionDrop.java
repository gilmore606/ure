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

    public UActionDrop(UThing thething) {
        thing = thething;
        destination = null;
    }
    public UActionDrop(UThing thething, UContainer thedest) {
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
