package ure.actions;

import ure.UCommander;
import ure.actors.UActor;


/**
 * Actor interacts with an interactable terrain, thing, or actor.
 *
 * This will simply call the target's .interactionFrom() method.
 *
 */
public class ActionInteract extends UAction {

    public static String id = "INTERACT";

    public static String noInteractMsg = "There's nothing there to interact with.";

    Interactable target;
    float timeTaken = 0f;

    public ActionInteract(UActor theactor, Interactable thetarget) {
        super(theactor);
        target = thetarget;
    }

    @Override
    public void doMe() {
        if (target.isInteractable(actor))
            timeTaken = target.interactionFrom(actor);
        else {
            commander.printScroll(noInteractMsg);
            suppressEvent();
        }
    }

    @Override
    public float timeCost() {
        return timeTaken;
    }
}
