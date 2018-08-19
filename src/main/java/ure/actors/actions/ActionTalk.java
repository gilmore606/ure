package ure.actors.actions;

import ure.actors.UActor;
import ure.ui.particles.ParticleTalk;
import ure.ui.sounds.Sound;

public class ActionTalk extends UAction {

    public static String id = "TALK";

    String text;
    String[] sounds = {"sounds/talking.wav"};

    public ActionTalk(UActor theactor, String thetext) {
        super(theactor);
        sound = new Sound(new String[]{"sounds/talking.wav"},0.5f);
        text = thetext;
    }

    @Override
    void doMe() {
        actor.say(text);
        actor.area().addParticle(new ParticleTalk(actor.areaX(), actor.areaY()));
    }
}
