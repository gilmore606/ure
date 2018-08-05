package ure.commands;

import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.things.UThing;
import ure.ui.modals.HearModalEntityPick;
import ure.ui.modals.UModalEntityPick;

import java.util.ArrayList;
import java.util.Iterator;

public class CommandUse extends UCommand implements HearModalEntityPick {

    public static final String id = "USE";

    public static String useNothingMsg = "You aren't carrying anything you can use.";
    public static String useDialog = "Use what?";
    public CommandUse() {
        super(id);
    }

    @Override
    public void execute(UPlayer player) {
        ArrayList<Entity> things =  new ArrayList<>();
        Iterator<UThing> i = player.iterator();
        while (i.hasNext()) {
            UThing t = i.next();
            if (t.isUsable(player))
                things.add((Entity)t);
        }
        if (things.isEmpty()) {
            commander.printScroll(useNothingMsg);
            return;
        }
        UModalEntityPick modal = new UModalEntityPick(useDialog, null, 0, 0, things,
                true, true, true, false, this, "use");
        commander.showModal(modal);
    }

    public void hearModalEntityPick(String context, Entity thing) {
        if (thing != null) {
            ((UThing)thing).useFrom(commander.player());
        }
    }
}
