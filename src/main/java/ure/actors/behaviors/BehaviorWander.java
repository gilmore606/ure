package ure.actors.behaviors;

import ure.actions.ActionWalk;
import ure.actions.UAction;
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
        int dir = commander.random.nextInt(4);
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
        UAction act = new ActionWalk(actor, wx,wy);
        currentUrgency = 1f;
        currentStatus = "wandering";
        return act;
    }
}
