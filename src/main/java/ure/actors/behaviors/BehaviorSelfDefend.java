package ure.actors.behaviors;

import ure.actors.UActor;
import ure.actors.UNPC;
import ure.actors.actions.UAction;
import ure.sys.Entity;

import java.util.ArrayList;

public class BehaviorSelfDefend extends UBehavior {

    public static final String TYPE = "selfdefend";

    ArrayList<Long> attackers;

    public BehaviorSelfDefend() {
        super(TYPE);
        attackers = new ArrayList<>();
    }

    @Override
    public UAction action(UNPC actor) {
        for (Entity entity : actor.seenEntities) {
            if (entity instanceof UActor) {
                if (attackers.contains(entity.getID())) {
                    currentUrgency = 1f;
                    return ForF(actor, (UActor)entity);
                }
            }
        }
        return null;
    }



    public ArrayList<Long> getAttackers() { return attackers; }
    public void setAttackers(ArrayList<Long> a) { attackers = a; }
}
