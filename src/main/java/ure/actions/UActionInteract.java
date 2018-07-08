package ure.actions;

import ure.actors.UActor;

/**
 * Actor interacts with an interactable terrain, thing, or actor.
 *
 * This will simply call the target's .interactionFrom() method.
 *
 */
public class UActionInteract extends UAction {

    public static String id = "INTERACT";

    public static String noInteractMsg = "There's nothing there to interact with.";

    Interactable target;
    float timeTaken = 0f;

    public UActionInteract(UActor theactor, Interactable thetarget) {
        actor = theactor;
        target = thetarget;
    }

    @Override
    public void doMe() {
        if (target.isInteractable(actor))
            timeTaken = target.interactionFrom(actor);
        else
            actor.commander().printScroll(noInteractMsg);
    }

    @Override
    public float timeCost() {
        return timeTaken;
    }
}
