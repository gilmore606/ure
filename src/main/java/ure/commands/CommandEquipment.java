package ure.commands;

import ure.actors.UPlayer;
import ure.sys.Entity;
import ure.ui.modals.UModalEquipment;

import java.util.ArrayList;

public class CommandEquipment extends UCommand {

    public static final String id = "EQUIPMENT";

    public CommandEquipment() { super(id); }

    @Override
    public void execute(UPlayer player) {
        UModalEquipment emodal = new UModalEquipment(player);
        emodal.setTitle("equipment");
        commander.showModal(emodal);
    }
}
