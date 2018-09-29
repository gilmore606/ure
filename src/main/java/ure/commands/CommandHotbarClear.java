package ure.commands;

import ure.actors.UPlayer;

public abstract class CommandHotbarClear extends UCommand {

    public static String id = "HC";

    public int slot;

    public CommandHotbarClear(String id, int slot) {
        super(id);
        this.slot = slot;
    }

    @Override
    public void execute(UPlayer player) {
        player.clearHotbarSlot(slot);
    }
}
