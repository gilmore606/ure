package ure.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.actions.ActionEmote;
import ure.actors.actions.Interactable;
import ure.actors.actions.UAction;
import ure.actors.behaviors.UBehavior;
import ure.math.UColor;
import ure.sys.Entity;
import ure.things.UThing;

import java.util.ArrayList;

/**
 * UNPC implements a non-player Actor with behaviors which initiate actions.
 *
 */
public class UNPC extends UActor implements Interactable {

    protected int visionRange = 12;
    protected String[] ambients;

    protected ArrayList<UBehavior> behaviors;

    @JsonIgnore
    public ArrayList<Entity> seenEntities;

    @Override
    public void initializeAsCloneFrom(UThing template) {
        if (template instanceof UNPC) {
            behaviors = new ArrayList<>();
            ArrayList<UBehavior> templateBehaviors = ((UNPC)template).getBehaviors();
            if (templateBehaviors != null) {
                for (UBehavior b : templateBehaviors) {
                    behaviors.add(b.makeClone());
                }
            }
        }
    }

    @Override
    public void act() {
        // Keep acting until we don't have any action time left.
        // You shouldn't override this.  You probably want nextAction().
        sleepCheck();
        if (!isAwake()) return;
        while (getActionTime() > 0f) {
            UAction action = nextAction();
            if (action == null) {
                this.setActionTime(0f);
                return;
            }
            if (area().closed) return;
            doAction(action);
        }
    }

    /**
     * Should we go to sleep?  Probably if the player's far away.
     */
    public void sleepCheck() {
        if (area() == null)
            stopActing();
        if (area().closed || area().closeRequested)
            stopActing();
        if (commander.player() == null || commander.player().area() != area()) {
            if (!area().getLabel().equals("TITLE"))
                stopActing();
        } else {
            wakeCheck(commander.player().areaX(), commander.player().areaY());
        }
    }


    @Override
    public void hearEvent(UAction action) {
        for (UBehavior behavior : behaviors) {
            behavior.hearEvent(this, action);
        }
    }

    UAction nextAction() {
        // What should we do next?  Override this for custom AI.
        //
        updateSeenEntities();
        UAction bestAction = null;
        float bestUrgency = 0f;
        int bc=0;
        for (UBehavior behavior : behaviors) {
            bc++;
            UAction action = behavior.action(this);
            if (action != null) {
                if (bestAction == null) {
                    bestAction = action;
                    bestUrgency = behavior.getCurrentUrgency();
                } else if (behavior.getCurrentUrgency() > bestUrgency) {
                    bestAction = action;
                    bestUrgency = behavior.getCurrentUrgency();
                }
            }
        }
        return bestAction;
    }
    UBehavior controllingBehavior() {
        float bestUrgency = 0f;
        UBehavior b = null;
        for (UBehavior behavior : behaviors) {
            if (behavior.getCurrentUrgency() > bestUrgency) {
                b = behavior;
                bestUrgency = behavior.getCurrentUrgency();
            }
        }
        return b;
    }

    void updateSeenEntities() {
        if (seenEntities == null)
            seenEntities = new ArrayList<>();
        else
            seenEntities.clear();
        for (int x=areaX()-getVisionRange();x<areaX()+getVisionRange();x++) {
            for (int y=areaY()-getVisionRange();y<areaY()+getVisionRange();y++) {
                UActor actor = area().actorAt(x,y);
                if (caresAbout(actor))
                    if (canSee(actor)) {
                        seenEntities.add(actor);
                        System.out.println(this.name + " (" + Long.toString(ID) + ") notices " + actor.getName() + "...");
                    }
            }
        }

    }

    boolean caresAbout(Entity entity) {
        if (entity == this) return false;
        for (UBehavior behavior : behaviors) {
            if (behavior.caresAbout(this, entity))
                return true;
        }
        return false;
    }

    @Override
    public String UIstatus() {
        UBehavior behavior = controllingBehavior();
        if (behavior != null) {
            return behavior.getCurrentStatus();
        }
        return super.UIstatus();
    }
    public UColor UIstatusColor() {
        UBehavior behavior = controllingBehavior();
        if (behavior != null) {
            return behavior.getCurrentStatusColor();
        }
        return super.UIstatusColor();
    }

    UAction Ambient() {
        return new ActionEmote(this, getAmbients()[commander.random.nextInt(getAmbients().length)]);
    }


    @Override
    public boolean isInteractable(UActor actor) {
        if (isHostileTo(actor))
            return false;
        for (UBehavior b : behaviors) {
            if (b.willInteractWith(this, actor))
                return true;
        }
        return false;
    }

    @Override
    public boolean isHostileTo(UActor actor) {
        for (UBehavior b : behaviors) {
            if (b.isHostileTo(this, actor))
                return true;
        }
        return false;
    }

    @Override
    public float interactionFrom(UActor actor) {
        for (UBehavior b : behaviors) {
            if (b.willInteractWith(this, actor))
                return b.interactionFrom(this, actor);
        }
        return 0.5f;
    }


    public int getVisionRange() {
        return visionRange;
    }

    public void setVisionRange(int visionRange) {
        this.visionRange = visionRange;
    }

    public String[] getAmbients() {
        return ambients;
    }

    public void setAmbients(String[] ambients) {
        this.ambients = ambients;
    }

    public ArrayList<UBehavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(ArrayList<UBehavior> behaviorObjects) {
        this.behaviors = behaviorObjects;
    }
}
