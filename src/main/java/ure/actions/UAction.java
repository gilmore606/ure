package ure.actions;

import ure.actors.UActor;

/**
 * UAction subclasses implement actions which Actors can perform to do arbitrary things in the
 * game world which take up game time, may be prevented by other Actors or the world, cause events,
 * and so on.
 *
 * Action instances are generally created by Actors in their act() methods to be returned to the
 * Commander to cause actions to occur.  These Action instances are also passed around to event
 * listeners to notify them of the action and its details.
 *
 */
public abstract class UAction {

    public static String id = "ACTION";

    public UActor actor;

    float cost = 1.0f;

    public UAction() {}
    /**
     * Override the constructor to set all parameters of your action when created.
     *
     * @param theactor
     */
    public UAction(UActor theactor) {
        actor = theactor;
    }

    /**
     * Do whatever it is that actor does, when actor does this.  Return the time it took,
     * modified by the actor.
     *
     * Do not override this.  Override doMe() to define your custom action's behavior.
     *
     * @return Time this action took to execute, in action time units.
     */
    public float doNow() {
        doMe();
        return timeCost();
    }

    /**
     * The time this action takes to execute, in action time units.
     *
     * Override this to define your action's duration.
     *
     * @return
     */
    public float timeCost() {
        return cost * (1f / actor.actionSpeed());
    }

    /**
     * Do what this action actually does.  Override this to create your action's custom behavior.
     *
     */
    void doMe() {

    }
}
