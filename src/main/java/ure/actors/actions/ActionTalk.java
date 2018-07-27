package ure.actors.actions;

import ure.actors.UActor;

public class ActionTalk extends UAction {

    public static String id = "TALK";

    String text;

    public ActionTalk(UActor theactor, String thetext) {
        actor = theactor;
        text = thetext;
    }

    @Override
    void doMe() { actor.say(text); }
}
