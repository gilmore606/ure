package ure.commands;

import ure.actions.ActionDrop;
import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.things.UThing;
import ure.ui.modals.HearModalEntityPick;
import ure.ui.modals.UModalEntityPick;

import java.util.ArrayList;
import java.util.Iterator;

public class CommandDrop extends UCommand implements HearModalEntityPick {

    public static final String id = "DROP";

    public static String dropNothingMsg = "You aren't carrying anything to drop.";
    public static String dropDialog = "Drop what?";

    public CommandDrop() {
        super(id);
    }

    @Override
    public void execute(UPlayer player) {
        if (!player.getContents().hasThings()) {
            commander.printScroll(dropNothingMsg);
            return;
        }
        ArrayList<Entity> things = new ArrayList<>();
        Iterator<UThing> i = player.iterator();
        while (i.hasNext())
            things.add((Entity)i.next());
        UModalEntityPick modal = new UModalEntityPick(dropDialog, null, 0, 0, things,
                true, true,this, "drop");
        commander.showModal(modal);
        // player.doAction(new UActionDrop(player));
    }

    public void hearModalEntityPick(String context, Entity thing) {
        if (thing != null) {
            commander.player().doAction(new ActionDrop(commander.player(), (UThing)thing));
        }
    }
}
