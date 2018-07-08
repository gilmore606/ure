package ure.actions;

import ure.actors.UActor;

public class UActionEmote extends UAction {

    public static String id = "EMOTE";

    String text;

    public UActionEmote(String thetext) {
        text = thetext;
    }

    @Override
    public void doFor(UActor actor) {
        actor.emote(text);
    }
}
