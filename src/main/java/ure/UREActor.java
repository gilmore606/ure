package ure;

import java.awt.*;

public class UREActor  extends UREThing {

    URECamera camera;

    public UREActor(String thename, char theicon, Color thecolor, boolean addOutline) {
        super(thename, theicon, thecolor, addOutline);
    }

    public void attachCamera(URECamera thecamera) {
        camera = thecamera;
        camera.addVisibilitySource(this);
    }
    public void detachCamera() {
        camera.removeVisibilitySource(this);
        camera = null;
    }

    public void walkDir(int xdir, int ydir) {
        int destX = xdir + areaX();
        int destY = ydir + areaY();
        if (location.containerType() == UContainer.TYPE_CELL) {
            if (area().willAcceptThing(this, destX, destY)) {
                moveToCell(area(), destX, destY);
            }
        }
    }

    @Override
    public void moveToCell(UREArea thearea, int destX, int destY) {
        if (camera != null) {
            camera.moveTo(area(), destX, destY);
        }
        super.moveToCell(thearea, destX, destY);
    }

    public void debug() {
        float sun = area().sunBrightnessAt(areaX(), areaY());
        System.out.println("sun " + Float.toString(sun));
        int[] light = camera.lightAtAreaXY(areaX(), areaY());
        System.out.println("light " + light.toString());
    }
}
