package ure.sys.events;

import ure.actors.UActor;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.things.UContainer;

public class DeathEvent {
    public String actorName;
    public UContainer location;
    public UActor killer;

    public DeathEvent(String actorName, UContainer location, UActor killer) {
        this.actorName = actorName;
        this.location = location;
        this.killer = killer;
    }
}
