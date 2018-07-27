package ure.actors.actions;

import ure.actors.UActor;
import ure.things.UThing;

/**
 * Actor uses a usable item in her inventory.
 *
 */
public class ActionUse extends UAction {

    public static String id = "USE";

    public static String noUseMsg = "You can't use that right now.";

    UThing target;
    float timeTaken = 0f;

    public ActionUse(UActor _actor, UThing _target) {
        super(_actor);
        target = _target;
    }

    @Override
    public void doMe() {
        if (target.isUsable(actor))
            timeTaken = target.useFrom(actor);
        else {
            if (actor == commander.player())
                commander.printScroll(noUseMsg);
            suppressEvent();
        }
    }

    @Override
    public float timeCost() {
        return timeTaken;
    }
}
