package ure.behaviors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.UActor;
import ure.sys.Entity;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actions.UAction;
import ure.actors.UNPC;

import javax.inject.Inject;

/**
 * UBehavior implements a source of actions for an NPC actor to perform, to emulate a behavior or achieve
 * a goal.  Subclass UBehavior to create behavior styles and patterns for your custom NPCs.
 *
 * A UBehavior is spawned and attached to a new NPC, and can maintain state about the NPC to help it
 * make decisions.
 *
 */
public abstract class UBehavior {

    @Inject
    @JsonIgnore
    UCommander commander;

    public UActor actor;        // the actor we're a part of

    protected String TYPE = "";

    float relativeUrgency;      // how urgent are we vs our NPC's other behaviors?
    float currentUrgency;       // urgency of our last action request
    String currentStatus;       // english status for UI, based on last action

    public UBehavior() {
        Injector.getAppComponent().inject(this);
        currentUrgency = 0f;
        currentStatus = "";
    }
    public UBehavior(String type) {
        this();
        this.TYPE = type;
    }

    /**
     * Override action() to return UActions on demand for the actor to perform.
     *
     * @param actor
     * @return
     */
    public UAction action(UNPC actor) {
        return null;
    }

    /**
     * Do we care about perceiving this entity?
     */
    public boolean caresAbout(Entity entity) {
        return false;
    }

    public float getRelativeUrgency() { return relativeUrgency; }
    public void setRelativeUrgency(float urg) { relativeUrgency = urg; }
    public float getCurrentUrgency() { return currentUrgency; }
    public void setCurrentUrgency(float urg) { currentUrgency = urg; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String status) { currentStatus = status; }
    public UActor getActor() { return actor; }
    public void setActor(UActor _actor) { actor = _actor; }
    public String getTYPE() { return TYPE; }
    public void setTYPE(String t) { TYPE = t; }
}
