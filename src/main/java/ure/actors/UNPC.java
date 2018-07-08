package ure.actors;

import ure.actions.UAction;
import ure.actions.UActionEmote;
import ure.actions.UActionWalk;
import ure.behaviors.UBehavior;

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
        while (actionTime > 0f) {
            UAction action = nextAction();
            if (action == null) {
                this.actionTime = 0f;
                return;
            }
            doAction(action);
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
        return new UActionWalk(this,wx,wy);
    }
    UAction HuntPlayer() {
        System.out.println(this.name + " hunting from " + Integer.toString(areaX()) + "," + Integer.toString(areaY()));
        int[] step = path.nextStep(area(), areaX(), areaY(), area().commander().player().areaX(), area().commander().player().areaY(), this, 25);
        if (step != null) {
            return new UActionWalk(this,step[0] - areaX(), step[1] - areaY());
        }
        return null;
    }

    UAction Ambient() {
        return new UActionEmote(this, ambients[random.nextInt(ambients.length)]);
    }
}
