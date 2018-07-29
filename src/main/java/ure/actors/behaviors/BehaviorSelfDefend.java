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

    @Override
    public boolean caresAbout(UNPC actor, Entity entity) {
        return isHostileTo(actor, entity);
    }

    @Override
    public void aggressionFrom(UNPC actor, UActor attacker) {
        long attackerID = attacker.getID();
        if (!attackers.contains(attackerID))
            attackers.add(attackerID);
    }

    @Override
    public boolean isHostileTo(UNPC actor, Entity target) {
        if (target instanceof UActor) {
            if (attackers.contains(target.getID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public UBehavior makeClone() {
        UBehavior clone = super.makeClone();
        if (clone != null)
            ((BehaviorSelfDefend)clone).setAttackers(new ArrayList<>());
        return clone;
    }

    public ArrayList<Long> getAttackers() { return attackers; }
    public void setAttackers(ArrayList<Long> a) { attackers = a; }
}
