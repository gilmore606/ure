package ure.actions;

import ure.actors.UREActor;

public class UAction {

    public static String id = "ACTION";

    float cost = 1.0f;

    public float doneBy(UREActor actor) {
        return cost * (1f / actor.actionSpeed());
    }
}
