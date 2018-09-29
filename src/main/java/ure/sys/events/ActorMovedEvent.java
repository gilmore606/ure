package ure.sys.events;

import ure.actors.UActor;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.things.UContainer;

public class ActorMovedEvent {
    public UActor actor;
    public UCell from, to;

    public ActorMovedEvent(UActor actor, UCell from, UCell to) {
        this.actor = actor;
        this.from = from;
        this.to = to;
    }
}
