package ure.things;

import ure.actors.UActor;

/**
 * A thing which can have other things put into it, or taken out of it.
 *
 */
public class Container extends UThing {

    public static final String TYPE = "container";

    public boolean openableBy(UActor actor) {
        return true;
    }
}
