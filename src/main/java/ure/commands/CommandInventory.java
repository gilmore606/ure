package ure.commands;

import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.things.UThing;
import ure.ui.modals.HearModalEntityPick;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalEntityPick;
import ure.ui.modals.UModalNotify;

import java.util.ArrayList;
import java.util.Iterator;

public class CommandInventory extends UCommand implements HearModalEntityPick {

    public static final String id = "INVENTORY";

    public CommandInventory() {
        super(id);
    }

    @Override
    public void execute(UPlayer player) {
        ArrayList<Entity> inventory = new ArrayList<>();
        Iterator<UThing> i = player.iterator();
        while (i.hasNext()) {
            UThing thing = i.next();
            if (!thing.isEquipped())
                inventory.add((Entity)thing);
        }
        UModal modal;
        if (inventory.isEmpty())
            modal = new UModalNotify("You aren't carrying anything.", 0, 0);
        else
             modal = new UModalEntityPick("You are carrying:", 0, 0, inventory, true,
                     true, true, (HearModalEntityPick)this, "inventory");
        modal.setTitle("inventory");
        commander.showModal(modal);
    }

    public void hearModalEntityPick(String callbackContext, Entity selection) {

    }
}
