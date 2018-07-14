package ure.actors;

import ure.actions.Interactable;
import ure.actions.UAction;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.math.UPath;
import ure.terrain.UTerrain;
import ure.things.Lightsource;
import ure.things.ThingI;
import ure.things.UContainer;
import ure.things.UThing;
import ure.ui.UCamera;

/**
 * UActor represents a UThing which can perform actions.  This includes the player and NPCs.
 *
 * Do not subclass UActor to change base actor behavior.  To change NPC-only behavior, use UBehaviors
 * or subclass NPC.  To change base actor behavior, use an ActorDeco decorator class.
 *
 */
public class UActor extends ThingI implements Interactable {

    public boolean awake = false;
    public int wakerange = 20;
    public int sleeprange = 30;
    public int sightrange = 15;

    public UCamera camera;
    int cameraPinStyle;

    int moveAnimX = 0;
    int moveAnimY = 0;
    int moveAnimDX = 0;
    int moveAnimDY = 0;

    float actionTime = 0f;

    @Override
    public void initialize() {
        super.initialize();
        setGlyphOutline(true);
    }

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
        if (getLocation().containerType() == UContainer.TYPE_CELL) {
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
    public boolean drawGlyphOutline() {
        if (isGlyphOutline())
            return true;
        if (commander.config.isOutlineActors())
            return true;
        return false;
    }

    @Override
    public int glyphOffsetX() {
        if (moveAnimX != 0)
            return moveAnimX;
        if (commander.config.getActorBounceAmount() > 0f)
            return bounceAnimX();
        return 0;
    }
    @Override
    public int glyphOffsetY() {
        if (moveAnimY != 0)
            return moveAnimY;
        if (commander.config.getActorBounceAmount() > 0f)
            return bounceAnimY();
        return 0;
    }

    public int bounceAnimX() {
        return 0;
    }
    public int bounceAnimY() {
        return -(int)(Math.abs(Math.sin((commander.frameCounter + areaX()*3 + areaY()*4) * commander.config.getActorBounceSpeed() * 0.1f)) * commander.config.getActorBounceAmount() * 5f);
    }

    public void animationTick() {
        if (moveAnimDX != 0 || moveAnimDY != 0) {
            moveAnimX += moveAnimDX;
            moveAnimY += moveAnimDY;
            if (moveAnimDX < 0 && moveAnimX < 0) moveAnimX = 0;
            if (moveAnimDX > 0 && moveAnimX > 0) moveAnimX = 0;
            if (moveAnimDY < 0 && moveAnimY < 0) moveAnimY = 0;
            if (moveAnimDY > 0 && moveAnimY > 0) moveAnimY = 0;
        }
    }

    @Override
    public void moveToCell(UArea thearea, int destX, int destY) {
        int oldx = -1;
        int oldy = -1;
        UArea oldarea = null;
        if (getLocation() != null) {
            oldx = areaX();
            oldy = areaY();
            oldarea = area();
        }
        super.moveToCell(thearea, destX, destY);
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
        int moveFrames = commander.config.getMoveAnimFrames();
        if (this instanceof UPlayer) moveFrames = commander.config.getMoveAnimPlayerFrames();
        if (oldx >=0 && oldarea == thearea && moveFrames > 0) {
            moveAnimX = (oldx-destX)*commander.config.getGlyphWidth();
            moveAnimY = (oldy-destY)*commander.config.getGlyphHeight();
            moveAnimDX = -(moveAnimX / moveFrames);
            moveAnimDY = -(moveAnimY / moveFrames);
        }
        thearea.cellAt(destX, destY).walkedOnBy(this);
    }

    public UCell myCell() {
        if (getLocation().containerType() == UContainer.TYPE_CELL)
            return (UCell) getLocation();
        return null;
    }
    public void debug() {
        Lightsource torch = (Lightsource)(commander.thingCzar.getThingByName("torch"));
        torch.moveToCell(area(), areaX(), areaY());
        torch.turnOn();
        commander.printScroll("You drop a torch.");
  }

    public void moveTriggerFrom(UActor actor) {
        if (actor instanceof UPlayer)
            commander.printScroll("Ow!");
    }

    // TODO: Parameterize all of these hardcoded strings somewhere
    public boolean tryGetThing(UThing thing) {
        if (thing == null) {
            commander.printScroll("Nothing to get.");
            return false;
        }
        if (thing.tryGetBy(this)) {
            thing.moveTo(this);
            if (this instanceof UPlayer)
                commander.printScroll("You pick up " + thing.getIname() + ".");
            else
                commander.printScrollIfSeen(this, this.getDnamec() + " picks up " + thing.getIname() + ".");
            thing.gotBy(this);
            return true;
        }
        return false;
    }

    public boolean tryDropThing(UThing thing, UContainer dest) {
        if (thing == null) {
            commander.printScroll("Nothing to drop.");
            return false;
        }
        if (dest.willAcceptThing(thing)) {
            thing.moveTo(dest);
            if (this instanceof UPlayer)
                commander.printScroll("You drop " + thing.getIname() + ".");
            else
                commander.printScrollIfSeen(this, this.getDnamec() + " drops " + thing.getIname() + ".");
            thing.droppedBy(this);
            return true;
        }
        return false;
    }

    public float actionSpeed() {
        return 1f;
    }

    public void doAction(UAction action) {
        if (action.allowedForActor() && !myCell().preventAction(action)) {
            float timecost = action.doNow();
            this.actionTime = this.actionTime - timecost;
            if (action.shouldBroadcastEvent())
                area().broadcastEvent(action);
        }
    }

    public void startActing() {
            commander.registerActor(this);
            awake = true;
            System.out.println(this.getName() + ": waking up!");
    }
    public void stopActing() {
        commander.unregisterActor(this);
        awake = false;
        System.out.println(this.getName() + ": going to sleep.");
    }

    public void act() {

    }

    /**
     * React to this action occuring in our awareness.
     *
     * @param action
     */
    public void hearEvent(UAction action) {

    }

    public void walkFail(UCell cell) {
        commander.printScroll(cell.terrain().bonkmsg());
    }

    /**
     * Can I see that thing from where I am (and I'm awake, and can see, etc)?
     *
     * @param thing
     * @return
     */
    public boolean canSee(UThing thing) {
        int x1 = areaX(); int y1 = areaY();
        int x2 = thing.areaX(); int y2 = thing.areaY();
        if (UPath.mdist(x1,y1,x2,y2) > sightrange)
            return false;
        if (!UPath.canSee(x1,y1,x2,y2,area(),this))
            return false;
        return true;
    }

    public void wakeCheck(int playerx, int playery) {
        if (getLocation() == null) return;
        int dist = Math.abs(areaX() - playerx) + Math.abs(areaY() - playery);
        if (awake && (sleeprange > 0) && (dist > sleeprange)) {
            stopActing();
        } else if (!awake && (wakerange > 0) && (dist < wakerange)) {
            startActing();
        }
    }

    public boolean willAcceptThing(UThing thing) {
        return true;
    }

    /**
     * Do I consider actor a mortal enemy?
     */
    public boolean isHostileTo(UActor actor) {
        return false;
    }

}
