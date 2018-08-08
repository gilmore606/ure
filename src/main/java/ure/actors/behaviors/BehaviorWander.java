package ure.actors.behaviors;

import ure.actors.actions.ActionWalk;
import ure.actors.actions.UAction;
import ure.actors.UNPC;

/**
 * Wander aimlessly.
 *
 */
public class BehaviorWander extends UBehavior {

    public static final String TYPE = "wander";

    public BehaviorWander() { super(TYPE);}

    @Override
    public UAction action(UNPC actor) {
        int dir = random.nextInt(8);
        int wx,wy;
        if (dir == 0) {
            wx = -1; wy = 0;
        } else if (dir == 1) {
            wx = 1; wy = 0;
        } else if (dir == 2) {
            wx = 0; wy = 1;
        } else if (dir == 3) {
            wx = 0;
            wy = -1;
        } else if (dir == 4) {
            wx = -1; wy = -1;
        } else if (dir == 5) {
            wx = 1; wy = 1;
        } else if (dir == 6) {
            wx = -1; wy = 1;
        } else {
            wx = 1; wy = -1;
        }
        UAction act = new ActionWalk(actor, wx,wy);
        currentUrgency = 0.5f;
        currentStatus = "wandering";
        return act;
    }
}
