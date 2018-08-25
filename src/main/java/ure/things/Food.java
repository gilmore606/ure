package ure.things;

import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.ui.sounds.Sound;

import java.util.ArrayList;

/**
 * A entity which can be consumed by an Actor, can cause statuses, and can rot.
 *
 */
public class Food extends UThing {

    public static final String TYPE = "food";

    public int bitesLeft;
    public int bites = 1;

    Sound sound;

    @Override
    public void initializeAsTemplate() {
        super.initializeAsTemplate();
        bitesLeft = bites;
        sound = new Sound("sounds/eating.wav");
    }

    @Override
    public boolean isUsable(UActor actor) {
        return true;
    }

    @Override
    public float useFrom(UActor actor) {
        return eatFrom(actor);
    }

    public float eatFrom(UActor actor) {
        bitesLeft--;
        if (actor instanceof UPlayer) {
            String message;
            if (bites == 1)
                message = "You eat " + getDname() + ".";
            else if (bitesLeft < 1)
                message = "You eat the last of " + getDname() + ".";
            else
                message = "You eat some " + getName() + ".";
            commander.printScroll(getIcon(), message);
        } else {
            commander.printScrollIfSeen(actor, actor.getDname() + " eats some " + getName() + ".");
        }
        if (sound != null)
            speaker.playWorld(sound,actor.areaX(),actor.areaY());
        onBite();
        if (bitesLeft < 1) {
            junk();
        }
        return 1f;
    }

    /**
     * Do whatever we do when someone takes a bite.
     */
    public void onBite() {

    }

    @Override
    public String useVerb() { return "eat"; }

    @Override
    public ArrayList<String> UIdetails(String context) {
        ArrayList<String> d = super.UIdetails(context);
        if (bitesLeft*3 < bites) {
            d.add("There's only a little left.");
        } else if (bitesLeft*2 < bites +1) {
            d.add("It's about half eaten.");
        } else if (bitesLeft < bites -1) {
            d.add("It's missing a few bites.");
        } else if (bitesLeft < bites) {
            d.add("It's missing a bite.");
        }
        return d;
    }
}
