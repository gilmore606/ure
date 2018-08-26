package ure.commands;

import ure.actors.UActor;
import ure.actors.actions.ActionGet;
import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.things.UThing;
import ure.ui.modals.HearModalEntityPickMulti;
import ure.ui.modals.UModalEntityPickMulti;

import java.util.ArrayList;

public class CommandGetMulti extends UCommand implements HearModalEntityPickMulti {

    public static final String id = "GETMULTI";

    public static String nothingToGetMsg = "There's nothing here to get.";
    public static String getDialog = "Get which items?";

    public CommandGetMulti() {
        super(id);
    }

    @Override
    public void execute(UPlayer player) {
        ArrayList<UThing> gettables = player.myCell().gettableThingsAt(player);
        if (gettables.isEmpty()) {
            commander.printScroll(nothingToGetMsg);
        } else if (gettables.size() == 1) {
            UThing thing = gettables.get(gettables.size()-1);
            player.doAction(new ActionGet(player, thing));
        } else {
            ArrayList<Entity> entities = new ArrayList<>();
            for (UThing thing : gettables)
                entities.add((Entity)thing);
            UModalEntityPickMulti modal = new UModalEntityPickMulti(getDialog, entities, true, this, "get");
            commander.showModal(modal);
        }
    }

    public void hearModalEntityPickMulti(String context, ArrayList<Entity> things) {
        for (Entity thing : things) {
            commander.player().doAction(new ActionGet(commander.player(), (UThing)thing));
        }
    }
}
