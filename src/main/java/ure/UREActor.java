package ure;

import ure.terrain.URETerrain;

import java.awt.*;

public class UREActor  extends UREThing {

    URECamera camera;
    int cameraPinStyle;

    public UREActor(String thename, char theicon, UColor thecolor, boolean addOutline) {
        super(thename, theicon, thecolor, addOutline);
    }

    public void attachCamera(URECamera thecamera, int pinstyle) {
        camera = thecamera;
        cameraPinStyle = pinstyle;
        camera.addVisibilitySource(this);
        camera.moveTo(area(), areaX(),  areaY());
    }
    public void detachCamera() {
        camera.removeVisibilitySource(this);
        camera = null;
    }

    public void walkDir(int xdir, int ydir) {
        int destX = xdir + areaX();
        int destY = ydir + areaY();
        if (location.containerType() == UContainer.TYPE_CELL) {
            if (!myTerrain().preventMoveFrom(this)) {
                if (area().isValidXY(destX, destY)) {
                    area().cellAt(destX, destY).moveTriggerFrom(this);
                }
            }
        }
    }

    public URETerrain myTerrain() {
        UCell c = area().cellAt(areaX(), areaY());
        if (c != null)
            return c.terrain();
        return null;
    }

    @Override
    public void moveToCell(UREArea thearea, int destX, int destY) {
        if (camera != null) {
            if (cameraPinStyle == URECamera.PINSTYLE_HARD)
                camera.moveTo(area(), destX, destY);
            if (cameraPinStyle == URECamera.PINSTYLE_SOFT) {
                int cameraX = Math.min(destX, thearea.xsize - camera.width / 2);
                int cameraY = Math.min(destY, thearea.ysize - camera.height / 2);
                cameraX = Math.max(camera.width / 2, cameraX);
                cameraY = Math.max(camera.height / 2, cameraY);
                camera.moveTo(area(), cameraX, cameraY);
            }
            if (cameraPinStyle == URECamera.PINSTYLE_SCREENS) {
                System.out.println("ERROR: Camera.PINSTYLE_SCREENS not implemented!");
            }
        }
        super.moveToCell(thearea, destX, destY);
        thearea.cellAt(destX, destY).walkedOnBy(this);
    }

    public void debug() {
        URELight newlight = new URELight(new UColor(0.9f,1f,0.7f), 15, 1);
        newlight.moveTo(area(), areaX(), areaY());
        area().commander().printScroll("You dropped a glowstick at " + Integer.toString(areaX()) + "," + Integer.toString(areaY()));
  }

    public void moveTriggerFrom(UREActor actor) {
        area().commander().printScroll("Ow!");
    }
}
