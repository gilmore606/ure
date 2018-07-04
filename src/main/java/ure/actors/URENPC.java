package ure.actors;

import ure.actions.UAction;
import ure.actions.UActionEmote;
import ure.actions.UActionWalk;

import java.util.Random;

public class URENPC extends UREActor {

    public int visionRange = 12;
    public int wakeRange = 20;
    public String[] ambients;

    Random random;

    @Override
    public void initialize() {
        super.initialize();
        random = new Random();
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

        float wut = random.nextFloat();

        if (wut < 0.1f) {
            return Ambient();
        } else if (wut < 0.7f) {
            return HuntPlayer();
        } else {
            return Wander();
        }
    }

    UAction HuntPlayer() {
        System.out.println("hunt from " + Integer.toString(areaX()) + "," + Integer.toString(areaY()));
        int[] step = path.nextStep(area(), areaX(), areaY(), area().commander().player().areaX(), area().commander().player().areaY(), this, 25);
        if (step != null) {
            return new UActionWalk(step[0] - areaX(), step[1] - areaY());
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
        return new UActionWalk(wx,wy);
    }

    UAction Ambient() {
        return new UActionEmote(ambients[random.nextInt(ambients.length)]);
    }
}
