package ure.math;

import com.google.common.eventbus.Subscribe;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.sys.Entity;
import ure.sys.events.ActorMovedEvent;

import java.util.HashSet;

public class DimapEntity extends Dimap {

    Entity target;
    int lastX, lastY;

    public DimapEntity(UArea area, int type, HashSet<String> moveTypes, Entity target) {
        super(area, type, moveTypes);
        this.target = target;
        lastX = -1;
        lastY = -1;
        bus.register(this);
    }

    public void changeEntity(Entity newtarget) {
        target = newtarget;
        dirty = true;
    }

    @Override
    public boolean targetsChanged() {
        UCell cell = this.target.location().cell();
        if (cell.x != lastX || cell.y != lastY)
            return true;
        return false;
    }

    @Override
    public void updateTargets() {
        UCell cell = this.target.location().cell();
        targets.add(new int[]{cell.x,cell.y});
        lastX = cell.x;
        lastY = cell.y;
    }

    @Subscribe
    public void actorMoved(ActorMovedEvent event) {
        if (event.actor == target)
            dirty = true;
    }
}
