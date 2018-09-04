package ure.commands;

import ure.actors.UPlayer;
import ure.actors.actions.ActionEquip;
import ure.actors.actions.ActionUnequip;
import ure.actors.actions.ActionUse;
import ure.actors.actions.UAction;
import ure.things.Container;
import ure.things.UThing;

import java.util.HashMap;

public abstract class CommandHotbar extends UCommand {

    public static String id = "H";

    public int slot;

    public CommandHotbar(String id, int slot) {
        super(id);
        this.slot = slot;
    }

    @Override
    public void execute(UPlayer player) {
        UThing thing = player.hotbar.get(slot);
        if (thing != null) {
            UAction action = null;
            if (thing.isUsable(player)) {
                action = new ActionUse(player, thing);
            } else if (thing.equipSlots != null) {
                if (thing.equipped)
                    action = new ActionUnequip(player, thing);
                else
                    action = new ActionEquip(player, thing);
            }
            if (action != null) {
                player.doAction(action);
            }
        }
    }
}
