package ure.actors.actions;

import ure.math.URandom;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actors.UActor;
import ure.ui.sounds.Sound;
import ure.ui.sounds.USpeaker;

import javax.inject.Inject;

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

    @Inject
    UCommander commander;
    @Inject
    URandom random;
    @Inject
    USpeaker speaker;

    public static String id = "ACTION";

    public String sounds[];
    public float soundgain;

    public UActor actor;

    Sound sound;
    float cost = 1.0f;
    boolean shouldBroadcastEvent = true;

    public UAction() {}
    /**
     * Override the constructor to set all parameters of your action when created.
     *
     * @param theactor
     */
    public UAction(UActor theactor) {
        Injector.getAppComponent().inject(this);
        actor = theactor;
        if (sounds != null)
            sound = new Sound(sounds, (soundgain > 0f) ? soundgain : 1f);
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
        if (sound != null)
            speaker.playWorld(sound, actor.areaX(), actor.areaY());
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
        return cost * (1f / actor.getActionspeed());
    }

    /**
     * Do what this action actually does.  Override this to create your action's custom behavior.
     *
     */
    void doMe() {

    }

    /**
     * Is actor actually allowed to do this action right now?
     * If false, give feedback why not.
     *
     * @return
     */
    public boolean allowedForActor() {
        return true;
    }

    /**
     * Prevent this action from broadcasting an event, because it was aborted, failed, etc.
     *
     */
    public void suppressEvent() {
        shouldBroadcastEvent = false;
    }
    public boolean shouldBroadcastEvent() { return shouldBroadcastEvent; }
}
