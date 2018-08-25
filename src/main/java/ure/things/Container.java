package ure.things;

import ure.actors.UActor;

/**
 * A entity which can have other entities put into it, or taken out of it.
 *
 */
public class Container extends UThing {

    public static final String TYPE = "container";

    public boolean openableBy(UActor actor) {
        return true;
    }

    @Override
    public String useVerb() { return "open"; }

    @Override
    public boolean isUsable(UActor actor) { return true; }

    @Override
    public float useFrom(UActor actor) {
        return openFrom(actor);
    }

    public float openFrom(UActor actor) {
        return 0f;
    }
}
