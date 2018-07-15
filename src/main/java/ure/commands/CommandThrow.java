package ure.commands;

import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.things.UThing;
import ure.ui.modals.HearModalEntityPick;
import ure.ui.modals.HearModalTarget;
import ure.ui.modals.UModalEntityPick;
import ure.ui.modals.UModalTarget;

import java.util.ArrayList;
import java.util.Iterator;

public class CommandThrow extends UCommand implements HearModalEntityPick, HearModalTarget {

    public static String throwNothingMsg = "You have nothing to throw.";
    public static String throwDialogMsg = "Throw what?";
    public static String throwTargetDialogMsg = "Select target.  Hold shift for free targeting.";
    public CommandThrow() {
        super();
        id = "THROW";
    }

    @Override
    public void execute(UPlayer player) {
        if (!player.getContents().hasThings()) {
            commander.printScroll(throwNothingMsg);
            return;
        }
        ArrayList<Entity> things = new ArrayList<>();
        Iterator<UThing> i = player.iterator();
        while (i.hasNext())
            things.add((Entity)i.next());
        UModalEntityPick modal = new UModalEntityPick(throwDialogMsg, null, 0, 0, things,
                true, true,this, "throw");
        commander.showModal(modal);
    }

    public void hearModalEntityPick(String context, Entity thing) {
        if (thing != null) {
            UModalTarget modal = new UModalTarget(throwTargetDialogMsg, this, "throw", null, true, true);
            commander.showModal(modal);
        }
    }

    public void hearModalTarget(String context, Entity target, int targetX, int targetY) {

    }
}
