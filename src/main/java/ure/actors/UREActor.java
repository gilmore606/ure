package ure.actors;

import ure.*;
import ure.actions.UAction;
import ure.terrain.URETerrain;
import ure.things.UREThing;

public class UREActor  extends UREThing {

    public boolean awake;

    public URECamera camera;
    int cameraPinStyle;
    UPath path;
    URECommander commander;

    float actionTime = 0f;

    public static boolean isActor = true;

    @Override
    public void initialize() {
        super.initialize();
        glyphOutline = true;
        path = new UPath();
    }

    @Override
    public boolean isActor() {
        return true;
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

    public void addActionTime(float amount) {
        actionTime += amount;
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

    public UCell myCell() {
        if (location.containerType() == UContainer.TYPE_CELL)
            return (UCell)location;
        return null;
    }
    public void debug() {
        URELight newlight = new URELight(new UColor(1f,1f,0.9f), 12, 3);
        newlight.moveTo(area(), areaX(), areaY());
        area().commander().printScroll("You dropped a glowstick at " + Integer.toString(areaX()) + "," + Integer.toString(areaY()));
  }

    public void moveTriggerFrom(UREActor actor) {
        area().commander().printScroll("Ow!");
    }

    public void tryGetThing(UREThing thing) {
        if (thing == null) {
            area().commander().printScroll("Nothing to get.");
            return;
        }
        if (thing.tryGetBy(this)) {
            thing.moveToContainer(this);
            if (isPlayer())
                area().commander().printScroll("You pick up " + thing.iname() + ".");
            else
                area().commander().printScrollIfSeen(this, this.dnamec() + " picks up " + thing.iname() + ".");
            thing.gotBy(this);
        }
    }

    public float actionSpeed() {
        return 1f;
    }

    public void doAction(UAction action) {
        float timecost = action.doneBy(this);
        this.actionTime = this.actionTime - timecost;
    }

    public void startActing(URECommander thecommander) {
        commander = thecommander;
        commander.registerActor(this);
    }
    public void stopActing() {
        commander.unRegisterActor(this);
    }

    public void act() {

    }

    public boolean canSee(UREThing thing) {
        return true;
    }
}
