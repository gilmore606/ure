package ure.actions;

import ure.actors.UActor;

public class UAction {

    public static String id = "ACTION";

    float cost = 1.0f;

    public float doneBy(UActor actor) {
        return cost * (1f / actor.actionSpeed());
    }
}
