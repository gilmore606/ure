package ure.actors;

import ure.actions.ActionEmote;
import ure.actions.UAction;
import ure.actions.ActionGet;
import ure.actions.ActionWalk;
import ure.behaviors.UBehavior;
import ure.math.UPath;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalNotify;

import java.util.ArrayList;
import java.util.Random;

/**
 * UNPC implements a non-player Actor with behaviors which initiate actions.
 *
 */
public class UNPC extends UActor {

    public int visionRange = 12;
    public String[] ambients;
    public String[] behaviors;

    ArrayList<UBehavior> behaviorObjects;

    public Random random;

    @Override
    public void initialize() {
        super.initialize();
        random = new Random();
        behaviorObjects = new ArrayList<UBehavior>();
    }

    @Override
    public void act() {
        // Keep acting until we don't have any action time left.
        // You shouldn't override this.  You probably want nextAction().
        while (getActionTime() > 0f) {
            UAction action = nextAction();
            if (action == null) {
                this.setActionTime(0f);
                return;
            }
            doAction(action);
        }
    }

    @Override
    public void hearEvent(UAction action) {
        if (action.actor != this) {
            if (action instanceof ActionGet) {
                emote(getDnamec() + " says, \"Hey that's mine!\"");
            }
        }
    }

    UAction nextAction() {
        // What should we do next?  Override this for custom AI.
        for (UBehavior behavior : behaviorObjects) {
            UAction action = behavior.action(this);
            if (action != null) return action;
        }


        float wut = random.nextFloat();
        if (wut < 0.1f) {
            if (ambients != null)
                return Ambient();
        } else if (wut < 0.5f) {
            return Wander();
        }
        return null;
    }
    UAction Wander() {
        int dir = random.nextInt(4);
        int wx,wy;
        if (dir == 0) {
            wx = -1; wy = 0;
        } else if (dir == 1) {
            wx = 1; wy = 0;
        } else if (dir == 2) {
            wx = 0; wy = 1;
        } else {
            wx = 0; wy = -1;
        }
        return new ActionWalk(this,wx,wy);
    }
    UAction HuntPlayer() {
        System.out.println(this.getName() + " hunting from " + Integer.toString(areaX()) + "," + Integer.toString(areaY()));
        int[] step = UPath.nextStep(area(), areaX(), areaY(), commander.player().areaX(), commander.player().areaY(), this, 25);
        if (step != null) {
            return new ActionWalk(this,step[0] - areaX(), step[1] - areaY());
        }
        return null;
    }

    UAction Ambient() {
        return new ActionEmote(this, ambients[random.nextInt(ambients.length)]);
    }


    @Override
    public boolean isInteractable(UActor actor) {
        return !isHostileTo(actor);
    }

    @Override
    public float interactionFrom(UActor actor) {
        UModal modal = new UModalNotify("\"Squeeek!\"", null, 2, 2);
        commander.showModal(modal);
        return 0.5f;
    }
}
