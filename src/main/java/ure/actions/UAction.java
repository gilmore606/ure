package ure.actions;

import ure.actors.UActor;

/**
 * UAction subclasses implement actions which Actors can perform to do arbitrary things in the
 * game world which take up game time, may be prevented by other Actors or the world, cause events,
 * and so on.
 *
 */
public abstract class UAction {

    public static String id = "ACTION";

    float cost = 1.0f;

    /**
     * Do whatever it is that actor does, when actor does this.  Return the time it took,
     * modified by the actor.
     *
     * Do not override this unless your action needs to figure its own cost on the fly;
     * otherwise, override doFor().
     *
     * @param actor
     * @return
     */
    public float doneBy(UActor actor) {
        doFor(actor);
        return cost * (1f / actor.actionSpeed());
    }

    /**
     * Do what this action actually does.
     *
     * @param actor
     */
    void doFor(UActor actor) {

    }
}
