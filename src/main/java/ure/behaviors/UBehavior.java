package ure.behaviors;

import ure.actions.UAction;
import ure.actors.UNPC;

/**
 * UBehavior implements a source of actions for an NPC actor to perform, to emulate a behavior or achieve
 * a goal.  Subclass UBehavior to create behavior styles and patterns for your custom NPCs.
 *
 * A UBehavior is spawned and attached to a new NPC, and can maintain state about the NPC to help it
 * make decisions.
 *
 */
public class UBehavior {

    public static String TYPE = "";

    /**
     * Override action() to return UActions on demand for the actor to perform.
     *
     * @param actor
     * @return
     */
    public UAction action(UNPC actor) {
        return null;
    }
}
