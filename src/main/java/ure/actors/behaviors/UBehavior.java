package ure.actors.behaviors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.actions.ActionWalk;
import ure.actors.UActor;
import ure.math.UColor;
import ure.math.UPath;
import ure.sys.Entity;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actors.actions.UAction;
import ure.actors.UNPC;
import ure.things.UThing;

import javax.inject.Inject;

/**
 * UBehavior implements a source of actions for an NPC actor to perform, to emulate a behavior or achieve
 * a goal.  Subclass UBehavior to create behavior styles and patterns for your custom NPCs.
 *
 * A UBehavior is spawned and attached to a new NPC, and can maintain state about the NPC to help it
 * make decisions.
 *
 */
public abstract class UBehavior implements Cloneable {

    @Inject
    @JsonIgnore
    UCommander commander;

    protected String TYPE = "";

    public float freq = 1f;          // how often should we even consider acting?

    float relativeUrgency;      // how urgent are we vs our NPC's other behaviors?
    float currentUrgency;       // urgency of our last action request
    String currentStatus;       // english status for UI, based on last action
    UColor currentStatusColor;

    public UBehavior() {
        Injector.getAppComponent().inject(this);
        currentUrgency = 0f;
        currentStatus = "";
        currentStatusColor = UColor.COLOR_GRAY;
    }
    public UBehavior(String type) {
        this();
        this.TYPE = type;
    }

    /**
     * If your Behavior has Object members you'll need to make new instances for those members by overriding this.
     */
    public UBehavior makeClone() {
        try {
            UBehavior clone = (UBehavior)clone();
            return clone;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Override action() to return UActions on demand for the actor to perform.
     *
     */
    public UAction action(UNPC actor) { return null; }

    public UAction getAction(UNPC actor) {
        currentStatus = "";
        currentUrgency = 0f;
        currentStatusColor = UColor.COLOR_GRAY;
        if (commander.random.nextFloat() > freq) return null;
        return action(actor);
    }

    /**
     * Do we care about perceiving this entity?
     */
    public boolean caresAbout(UNPC actor, Entity entity) {
        return false;
    }

    /**
     * Are we hostile to this entity?
     *
     */
    public boolean isHostileTo(UNPC actor, Entity entity) {
        return false;
    }

    /**
     * React to an event we saw.
     */
    public void hearEvent(UNPC actor, UAction action) {

    }
    /**
     * Notice and possibly remember aggression from attacker.
     */
    public void aggressionFrom(UNPC actor, UActor attacker) {

    }
    /**
     * Will I let actor Interact with me?
     */
    public boolean willInteractWith(UNPC actor, UActor interactor) {
        return false;
    }

    /**
     * Receive an Interact from interactor.
     * Return the actionTime it took.
     */
    public float interactionFrom(UNPC actor, UActor interactor) {
        return 0.5f;
    }

    /**
     * The following utility methods are for use in custom Behavior.action() as shortcuts to certain common
     * responses to situations.
     *
     */

    /**
     * Step toward it.
     */
    public UAction Approach(UNPC actor, Entity entity) {
        int[] step = UPath.nextStep(actor.area(), actor.areaX(), actor.areaY(),
                entity.areaX(), entity.areaY(), actor, 25);
        if (step != null)
            return new ActionWalk(actor, step[0] - actor.areaX(), step[1] - actor.areaY());
        return null;
    }

    /**
     * Go and get it.
     */
    public UAction Get(UNPC actor, UThing thing) {
        return null;
    }

    /**
     * Go and kill it.
     */
    public UAction Attack(UNPC actor, UActor target) {
        currentStatus = "hostile";
        currentStatusColor = UColor.COLOR_RED;
        currentUrgency = 0.8f;
        if (UPath.mdist(actor.areaX(),actor.areaY(),target.areaX(),target.areaY()) > 1)
            return Approach(actor, target);
        actor.emote(actor.getName() + " flails ineffectually at " + target.getName() + ".");
        return null;
    }

    /**
     * Get away from it.
     */
    public UAction Avoid(UNPC actor, Entity entity) {
        return null;
    }

    /**
     * Respond to threat from it (by fight or flight).
     */
    public UAction ForF(UNPC actor, UActor threat) {
        return Attack(actor, threat);
    }

    public float getRelativeUrgency() { return relativeUrgency; }
    public void setRelativeUrgency(float urg) { relativeUrgency = urg; }
    public float getCurrentUrgency() { return currentUrgency; }
    public void setCurrentUrgency(float urg) { currentUrgency = urg; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String status) { currentStatus = status; }
    public UColor getCurrentStatusColor() { return currentStatusColor; }
    public void setCurrentStatusColor(UColor c) { currentStatusColor = c; }
    public String getTYPE() { return TYPE; }
    public void setTYPE(String t) { TYPE = t; }
    public float getFreq() { return freq; }
    public void setFreq(Float f) { freq = f; }
}
