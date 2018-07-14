package ure.commands;

import ure.actors.UPlayer;
import ure.ui.modals.UModalNotify;

public class CommandInventory extends UCommand {

    public static String id = "INVENTORY";

    @Override
    public void execute(UPlayer player) {
        UModalNotify modal = new UModalNotify("No inventory implemented yet.", null);
        commander.showModal(modal);
    }
}
