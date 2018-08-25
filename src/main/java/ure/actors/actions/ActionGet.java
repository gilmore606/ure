package ure.actors.actions;

import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.things.UThing;

/**
 * Actor tries to pick up the entity, from the ground or a container.
 *
 */
public class ActionGet extends UAction {

    public static String id = "GET";

    UThing thing;

    public ActionGet(UActor theactor, UThing thething) {
        actor = theactor;
        thing = thething;
    }

    @Override
    void doMe() {
        if (!actor.tryGetThing(thing))
            suppressEvent();
    }
}
