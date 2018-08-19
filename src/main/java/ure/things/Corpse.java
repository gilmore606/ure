package ure.things;

import ure.actors.UActor;

public class Corpse extends Food {

    public static final String TYPE = "corpse";

    public static final String namePrefix = "dead ";
    public static final float weightRatio = 0.5f;

    public void become(UActor actor) {
        setName(namePrefix + actor.name());
        setWeight((int)((float)actor.getWeight() * weightRatio));
        setIcon(actor.icon());
        setDescription("It's just like " + actor.getIname() + ".  Except dead.");
        icon.setEntity(this);
        icon.fgColor.desaturateBy(config.getDefaultCorpseDesaturation());
    }
}
