package ure.actors.actions;

import ure.actors.UActor;
import ure.ui.particles.ParticleTalk;

public class ActionTalk extends UAction {

    public static String id = "TALK";

    String text;

    public ActionTalk(UActor theactor, String thetext) {
        super(theactor);
        text = thetext;
        sounds = new String[]{"sounds/talking.wav"};
    }

    @Override
    void doMe() {
        actor.say(text);
        actor.area().addParticle(new ParticleTalk(actor.areaX(), actor.areaY()));
    }
}
