package ure.actors.behaviors;

import ure.actors.UActor;
import ure.actors.actions.ActionTalk;
import ure.actors.actions.UAction;
import ure.actors.UNPC;
import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.ui.modals.UModalNotify;

/**
 * I can talk and be talked to (by and to the player, mostly).
 */
public class BehaviorTalking extends UBehavior {

    public static final String TYPE = "talking";

    float babbleChance = 0.1f;

    String babbles[];

    public BehaviorTalking() {
        super(TYPE);
        babbles = new String[]{"Yawn.","I'm bored.","This place sucks.","Found any cool wands lately?","Another day, another gold piece."};
    }

    @Override
    public UAction action(UNPC actor) {
        currentStatus = "";
        if (commander.random.nextFloat() > babbleChance) {
            return null;
        }
        UAction action = null;
        UPlayer player = null;
        for (Entity entity : actor.seenEntities) {
            if (entity instanceof UPlayer) {
                player = (UPlayer)entity;
                currentStatus = "talking";
                currentUrgency = 0.3f;
                action = new ActionTalk(actor, babbleAt(actor,player));
            }
        }
        return action;
    }

    @Override
    public boolean caresAbout(UNPC actor, Entity entity) {
        if (entity instanceof UPlayer) {
            return true;
        }
        return false;
    }

    String babbleAt(UNPC actor, UActor target) {
        if (actor.isHostileTo(target)) {
            return("I'm gonna kill you!");
        } else {
            return babbles[commander.random.nextInt(babbles.length-1)];
        }
    }

    @Override
    public boolean willInteractWith(UNPC actor, UActor interactor) {
        if (interactor instanceof UPlayer)
            return true;
        return false;
    }

    @Override
    public float interactionFrom(UNPC actor, UActor interactor) {
        String text = actor.getDescription() + "\n \n\"" + babbleAt(actor, interactor) + "\"";
        UModalNotify nmodal = new UModalNotify(text, null, 1, 1);
        commander.showModal(nmodal);
        return 0.5f;
    }
}
