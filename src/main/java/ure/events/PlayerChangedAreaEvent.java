package ure.events;

import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.terrain.Stairs;

public class PlayerChangedAreaEvent {
    public UPlayer player;
    public Stairs stairs;
    public UArea sourceArea;
    public UArea destArea;

    public PlayerChangedAreaEvent(UPlayer player, Stairs stairs, UArea sourceArea, UArea destArea) {
        this.player = player;
        this.stairs = stairs;
        this.sourceArea = sourceArea;
        this.destArea = destArea;
    }
}
