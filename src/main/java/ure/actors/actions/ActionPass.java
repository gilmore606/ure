package ure.actors.actions;

import ure.actors.UActor;

/**
 * Actor does nothing for one action-time unit.
 *
 */
public class ActionPass extends UAction {

    public static String id = "PASS";

    public ActionPass(UActor theactor) {
        actor = theactor;
    }

    @Override
    void doMe() {

    }
}
