package ure.actions;

import ure.actors.UActor;

/**
 * Actor makes an emotive text noise.
 *
 */
public class UActionEmote extends UAction {

    public static String id = "EMOTE";

    String text;

    public UActionEmote(String thetext) {
        text = thetext;
    }

    @Override
    void doFor(UActor actor) {
        actor.emote(text);
    }
}
