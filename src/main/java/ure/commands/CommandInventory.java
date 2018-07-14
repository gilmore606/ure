package ure.commands;

import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.things.UThing;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalEntityPick;
import ure.ui.modals.UModalNotify;

import java.util.ArrayList;
import java.util.Iterator;

public class CommandInventory extends UCommand {

    public CommandInventory() {
        super();
        id = "INVENTORY";
    }

    @Override
    public void execute(UPlayer player) {
        //UModalNotify modal = new UModalNotify("No inventory implemented yet.", null, 2, 2);
        ArrayList<Entity> inventory = new ArrayList<>();
        Iterator<UThing> i = player.iterator();
        while (i.hasNext())
            inventory.add((Entity)i.next());
        UModal modal;
        if (inventory.isEmpty())
            modal = new UModalNotify("You aren't carrying anything.", null, 1, 3);
        else
             modal = new UModalEntityPick("You are carrying:", null, 2, 2, inventory, null, null);
        commander.showModal(modal);
    }
}
