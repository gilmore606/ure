package ure.things;

import ure.actors.UActor;

/**
 * A thing which can be consumed by an Actor, can cause statuses, and can rot.
 *
 */
public class Food extends UThing {

    public static final String TYPE = "food";

    @Override
    public boolean isUsable(UActor actor) {
        return true;
    }

    @Override
    public String useVerb() { return "eat"; }

}
