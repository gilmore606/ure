package ure.actors;

import ure.*;
import ure.actions.UAction;
import ure.terrain.UTerrain;
import ure.things.Lightsource;
import ure.things.ThingI;
import ure.things.UThing;
import ure.ui.UCamera;

public class UActor extends ThingI {

    public boolean awake = false;
    public int wakerange = 20;
    public int sleeprange = 30;

    public UCamera camera;
    int cameraPinStyle;
    UPath path;
    UCommander commander;

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

    public UCommander commander() { return commander; }

    public float actionTime() {
        return actionTime;
    }

    public void attachCamera(UCamera thecamera, int pinstyle) {
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
        actionTime = actionTime + amount;
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

    public UTerrain myTerrain() {
        UCell c = area().cellAt(areaX(), areaY());
        if (c != null)
            return c.terrain();
        return null;
    }

    @Override
    public void moveToCell(UArea thearea, int destX, int destY) {
        if (camera != null) {
            if (cameraPinStyle == UCamera.PINSTYLE_HARD)
                camera.moveTo(area(), destX, destY);
            if (cameraPinStyle == UCamera.PINSTYLE_SOFT) {
                int cameraX = Math.min(destX, thearea.xsize - camera.columns / 2);
                int cameraY = Math.min(destY, thearea.ysize - camera.rows / 2);
                cameraX = Math.max(camera.columns / 2, cameraX);
                cameraY = Math.max(camera.rows / 2, cameraY);
                camera.moveTo(area(), cameraX, cameraY);
            }
            // TODO: implement binding of isaac style camera move by screens
            if (cameraPinStyle == UCamera.PINSTYLE_SCREENS) {
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
        Lightsource torch = (Lightsource)commander.thingCzar.getThingByName("torch");
        torch.moveToCell(area(), areaX(), areaY());
        torch.turnOn();
        area().commander().printScroll("You drop a torch.");
  }

    public void moveTriggerFrom(UActor actor) {
        if (actor.isPlayer())
            area().commander().printScroll("Ow!");
    }

    public void tryGetThing(UThing thing) {
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

    public void startActing(UCommander thecommander) {
        commander = thecommander;
        if (commander != null) {
            commander.registerActor(this);
            awake = true;
            System.out.println(this.name + ": waking up!");
        }
    }
    public void stopActing() {
        commander.unRegisterActor(this);
        awake = false;
        System.out.println(this.name + ": going to sleep.");
    }

    public void act() {

    }

    public boolean canSee(UThing thing) {
        return true;
    }

    public void wakeCheck(int playerx, int playery) {
        if (location == null) return;
        int dist = Math.abs(areaX() - playerx) + Math.abs(areaY() - playery);
        if (awake && (sleeprange > 0) && (dist > sleeprange)) {
            stopActing();
        } else if (!awake && (wakerange > 0) && (dist < wakerange)) {
            startActing(area().commander());
        }
    }

    public boolean willAcceptThing(UThing thing) {
        return true;
    }
}