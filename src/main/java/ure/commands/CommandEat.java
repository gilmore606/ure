package ure.commands;

import ure.actors.UPlayer;
import ure.actors.actions.ActionUse;
import ure.actors.actions.UAction;
import ure.sys.Entity;
import ure.things.Food;
import ure.things.UThing;
import ure.ui.modals.HearModalEntityPick;
import ure.ui.modals.UModalEntityPick;

import java.util.ArrayList;

public class CommandEat extends UCommand implements HearModalEntityPick {

    public static final String id = "EAT";

    public static String nothingToEatMsg = "You don't have anything to eat.";
    public static String eatDialog = "Eat which?";

    public CommandEat() { super(id); }

    @Override
    public void execute(UPlayer player) {
        ArrayList<Entity> edibles = new ArrayList<>();
        for (UThing thing : player.getContents().getThings()) {
            if (thing.useVerb().equals("eat")) {
                edibles.add(thing);
            }
        }
        if (edibles.size() < 1)
            commander.printScroll(nothingToEatMsg);
        else if (edibles.size() == 1) {
            ActionUse action = new ActionUse(player, (UThing)edibles.get(0));
            player.doAction(action);
        } else {
            UModalEntityPick modal = new UModalEntityPick(eatDialog, null, 0, 0, edibles, true, true, false, false, this, "eat");
            commander.showModal(modal);
        }
    }

    public void hearModalEntityPick(String context, Entity selection) {
        ActionUse action = new ActionUse(commander.player(), (UThing)selection);
        commander.player().doAction(action);
    }
}
