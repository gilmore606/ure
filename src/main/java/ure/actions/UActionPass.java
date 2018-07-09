package ure.actions;

import ure.actors.UActor;

/**
 * Actor does nothing for one action-time unit.
 *
 */
public class UActionPass extends UAction {

    public static String id = "PASS";

    public UActionPass(UActor theactor) {
        actor = theactor;
    }

    @Override
    void doMe() {

    }
}
