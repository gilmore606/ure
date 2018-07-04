package ure.actors;

import ure.actions.UAction;
import ure.actions.UActionEmote;
import ure.actions.UActionWalk;
import ure.behaviors.UBehavior;

import java.util.ArrayList;
import java.util.Random;

public class URENPC extends UREActor {

    public int visionRange = 12;
    public int wakeRange = 20;
    public String[] ambients;

    ArrayList<UBehavior> behaviors;

    public Random random;

    @Override
    public void initialize() {
        super.initialize();
        random = new Random();
        behaviors = new ArrayList<UBehavior>();
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
        for (UBehavior behavior : behaviors) {
            UAction action = behavior.action(this);
            if (action != null) return action;
        }


        float wut = random.nextFloat();

        if (wut < 0.1f) {
            return Ambient();
        } else if (wut < 0.7f) {
            return HuntPlayer();
        }
        return null;
    }

    UAction HuntPlayer() {
        System.out.println(this.name + " hunting from " + Integer.toString(areaX()) + "," + Integer.toString(areaY()));
        int[] step = path.nextStep(area(), areaX(), areaY(), area().commander().player().areaX(), area().commander().player().areaY(), this, 25);
        if (step != null) {
            return new UActionWalk(step[0] - areaX(), step[1] - areaY());
        }
        return null;
    }

    UAction Ambient() {
        return new UActionEmote(ambients[random.nextInt(ambients.length)]);
    }
}
