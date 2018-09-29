package ure.math;

import ure.areas.UArea;
import ure.areas.gen.Shape;

import java.util.HashSet;

public class DimapShape extends Dimap {

    Shape target;

    public DimapShape(UArea area, int type, HashSet<String> moveTypes, Shape target) {
        super(area, type, moveTypes);
        this.target = target;
    }

    public void changeShape(Shape newtarget) {
        target = newtarget;
        dirty = true;
    }

    @Override
    public boolean targetsChanged() {
        return false;
    }

    @Override
    public void updateTargets() {
        for (int x=0;x<target.xsize;x++) {
            for (int y=0;y<target.ysize;y++) {
                if (target.value(x,y)) {
                    map[x][y] = 0f;
                    if (!target.value(x-1,y) || !target.value(x+1,y) || !target.value(x,y-1) || !target.value(x,y+1)) {
                        targets.add(new int[]{x,y});
                    }
                }
            }
        }
    }
}
