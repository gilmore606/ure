package ure.actions;

import ure.actors.UActor;

/**
 * Actor makes an emotive text noise.
 *
 */
public class ActionEmote extends UAction {

    public static String id = "EMOTE";

    String text;

    public ActionEmote(UActor theactor, String thetext) {
        actor = theactor;
        text = thetext;
    }

    @Override
    void doMe() {
        actor.emote(text);
    }
}
