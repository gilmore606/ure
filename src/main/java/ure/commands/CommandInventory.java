package ure.commands;

import ure.actors.UPlayer;
import ure.ui.modals.UModalNotify;

public class CommandInventory extends UCommand {

    public CommandInventory() {
        super();
        id = "INVENTORY";
    }

    @Override
    public void execute(UPlayer player) {
        UModalNotify modal = new UModalNotify("No inventory implemented yet.", null, 2, 2);
        commander.showModal(modal);
    }
}
