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
public class BehaviorTalker extends UBehavior {

    public static final String TYPE = "talker";

    public String comments[];
    public String responses[];

    public BehaviorTalker() {
        super(TYPE);
    }

    @Override
    public UAction action(UNPC actor) {
        UAction action = null;
        UPlayer player = null;
        for (Entity entity : actor.seenEntities) {
            if (entity instanceof UPlayer) {
                player = (UPlayer)entity;
                currentStatus = "talking";
                currentUrgency = 0.3f;
                action = new ActionTalk(actor, commentTo(actor,player));
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

    String commentTo(UNPC actor, UActor target) {
        if (actor.isHostileTo(target)) {
            return("I'm gonna kill you!");
        } else {
            return comments[random.i(comments.length)];
        }
    }

    String responseTo(UNPC actor, UActor target) {
        return responses[random.i(responses.length)];
    }

    @Override
    public boolean willInteractWith(UNPC actor, UActor interactor) {
        if (interactor instanceof UPlayer)
            return true;
        return false;
    }

    @Override
    public float interactionFrom(UNPC actor, UActor interactor) {
        String text = actor.getDescription() + "\n \n\"" + responseTo(actor, interactor) + "\"";
        UModalNotify nmodal = new UModalNotify(text);
        nmodal.setPad(1,1);
        commander.showModal(nmodal);
        return 0.5f;
    }

    public String[] getComments() { return comments; }
    public String[] getResponses() { return responses; }
    public void setComments(String[] c) { comments = c; }
    public void setResponses(String[] r) { responses = r; }
}
