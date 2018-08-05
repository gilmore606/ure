package ure.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;
import ure.actors.actions.ActionWalk;
import ure.actors.actions.Interactable;
import ure.actors.actions.UAction;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.math.UColor;
import ure.math.UPath;
import ure.terrain.UTerrain;
import ure.things.Lightsource;
import ure.things.UThing;
import ure.things.UContainer;
import ure.ui.UCamera;
import ure.ui.particles.ParticleHit;

import java.util.ArrayList;

/**
 * UActor represents a UThing which can perform actions.  This includes the player and NPCs.
 *
 * Do not subclass UActor to change base actor behavior.  To change NPC-only behavior, use UBehaviors
 * or subclass NPC.  To change base actor behavior, use an ActorDeco decorator class.
 *
 */
public class UActor extends UThing implements Interactable {

    protected boolean awake = false;
    protected int wakerange = 12;
    protected int sleeprange = 16;
    protected int sightrange = 9;
    protected float actionspeed = 1f;
    protected float movespeed = 1f;
    protected String bodytype = "humanoid";
    protected Body body;

    @JsonIgnore
    protected UCamera camera; // TODO: Reconnect after deserialization

    protected int cameraPinStyle;

    protected int moveAnimX = 0;
    protected int moveAnimY = 0;
    protected int moveAnimDX = 0;
    protected int moveAnimDY = 0;

    protected float actionTime = 0f;

    @Override
    public void initializeAsTemplate() {
        super.initializeAsTemplate();
        setGlyphOutline(true);
        body = actorCzar.getNewBody(bodytype);
    }

    public float actionTime() {
        return getActionTime();
    }

    public void attachCamera(UCamera thecamera, int pinstyle) {
        camera = thecamera;
        setCameraPinStyle(pinstyle);
        camera.addVisibilitySource(this);
        if (area() != null)
            camera.moveTo(area(), areaX(),  areaY());
    }
    public void detachCamera() {
        camera.removeVisibilitySource(this);
        camera = null;
    }

    public void addActionTime(float amount) {
        setActionTime(getActionTime() + amount);
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
        if (getMoveAnimX() != 0)
            return getMoveAnimX();
        if (commander.config.getActorBounceAmount() > 0f)
            return bounceAnimX();
        return 0;
    }
    @Override
    public int glyphOffsetY() {
        if (getMoveAnimY() != 0)
            return getMoveAnimY();
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
        if (getMoveAnimDX() != 0 || getMoveAnimDY() != 0) {
            setMoveAnimX(getMoveAnimX() + getMoveAnimDX());
            setMoveAnimY(getMoveAnimY() + getMoveAnimDY());
            if (getMoveAnimDX() < 0 && getMoveAnimX() < 0) setMoveAnimX(0);
            if (getMoveAnimDX() > 0 && getMoveAnimX() > 0) setMoveAnimX(0);
            if (getMoveAnimDY() < 0 && getMoveAnimY() < 0) setMoveAnimY(0);
            if (getMoveAnimDY() > 0 && getMoveAnimY() > 0) setMoveAnimY(0);
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
        // TODO: Move the following logic to UCamera
        if (camera != null) {
            if (getCameraPinStyle() == UCamera.PINSTYLE_HARD)
                camera.moveTo(area(), destX, destY);
            if (getCameraPinStyle() == UCamera.PINSTYLE_SOFT) {
                int cameraX = Math.min(destX, thearea.xsize - camera.columns / 2);
                int cameraY = Math.min(destY, thearea.ysize - camera.rows / 2);
                cameraX = Math.max(camera.columns / 2, cameraX);
                cameraY = Math.max(camera.rows / 2, cameraY);
                camera.moveTo(area(), cameraX, cameraY);
            }
            // TODO: implement binding of isaac style camera move by screens
            if (getCameraPinStyle() == UCamera.PINSTYLE_SCREENS) {
                System.out.println("ERROR: Camera.PINSTYLE_SCREENS not implemented!");
            }
        }
        int moveFrames = commander.config.getMoveAnimFrames();
        if (this instanceof UPlayer) moveFrames = commander.config.getMoveAnimPlayerFrames();
        if (oldx >=0 && oldarea == thearea && moveFrames > 0) {
            setMoveAnimX((oldx-destX)*commander.config.getGlyphWidth());
            setMoveAnimY((oldy-destY)*commander.config.getGlyphHeight());
            setMoveAnimDX(-(getMoveAnimX() / moveFrames));
            setMoveAnimDY(-(getMoveAnimY() / moveFrames));
        }
        if (oldarea == thearea)
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
        if (actor instanceof UPlayer) {
            aggressionFrom(actor);
            commander.printScroll(null, "You attack " + getDname() + "!", UColor.COLOR_LIGHTRED);
            area().addParticle(new ParticleHit(areaX(), areaY(), bloodColor(), 0.5f+commander.random.nextFloat()*0.5f));
        }
    }

    public void aggressionFrom(UActor actor) {

    }

    public UColor bloodColor() {
        return UColor.COLOR_RED;
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
                commander.printScroll(thing.getIcon(), "You pick up " + thing.getIname() + ".");
            else
                commander.printScrollIfSeen(this, StringUtils.capitalize(this.getDname()) + " picks up " + thing.getIname() + ".");
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
                commander.printScroll(thing.getIcon(), "You drop " + thing.getIname() + ".");
            else
                commander.printScrollIfSeen(this, StringUtils.capitalize(this.getDname()) + " drops " + thing.getIname() + ".");
            thing.droppedBy(this);
            return true;
        }
        return false;
    }

    public boolean tryEquipThing(UThing thing) {
        return thing.tryEquip(this);
    }

    public boolean tryUnequip(UThing thing) {
        return thing.tryUnequip(this);
    }

    public boolean freeEquipSlot(String slot, int slotcount) {
        int totalslots = body.slotsForPart(slot);
        if (totalslots < slotcount) return false;
        int freeslots = totalslots;
        ArrayList<UThing> equipped = equippedOn(slot);
        for (UThing thing : equipped) {
            freeslots -= thing.getEquipSlotCount();
        }
        while (freeslots < slotcount) {
            UThing unequipped = null;
            for (UThing thing : equipped) {
                if (tryUnequip(thing)) {
                    unequipped = thing;
                    break;
                }
            }
            equipped.remove(unequipped);
            freeslots += unequipped.getEquipSlotCount();
        }
        return true;
    }

    public ArrayList<UThing> equippedOn(String slot) {
        ArrayList<UThing> equipped = new ArrayList<>();
        for (UThing thing : contents.getThings()) {
            if (thing.equipped) {
                for (String s : thing.getEquipSlots())
                    if (s.equals(slot))
                        equipped.add(thing);
            }
        }
        return equipped;
    }
    public ArrayList<UThing> equipment() {
        ArrayList<UThing> equipped = new ArrayList<>();
        for (UThing thing : contents.getThings()) {
            if (thing.equipped)
                equipped.add(thing);
        }
        return equipped;
    }

    public void doAction(UAction action) {
        if (action.allowedForActor() && !myCell().preventAction(action)) {
            float timecost = action.doNow();
            this.setActionTime(this.getActionTime() - timecost);
            if (action.shouldBroadcastEvent())
                area().broadcastEvent(action);
        }
    }

    public void startActing() {
            commander.registerActor(this);
            setAwake(true);
    }
    public void stopActing() {
        commander.unregisterActor(this);
        setAwake(false);
        setActionTime(0f);
    }

    public void act() {

    }

    public void say(String text) {
        commander.printScrollIfSeen(this,StringUtils.capitalize(getDname()) + " says, \"" + text + "\"");
    }

    public boolean stepToward(int x, int y) {
        int[] step = UPath.nextStep(area(), areaX(), areaY(), x, y, this, 100);
        if (step != null) {
            ActionWalk action = new ActionWalk(this, step[0] - areaX(), step[1] - areaY());
            doAction(action);
            return true;
        }
        return false;
    }

    /**
     * React to this action occuring in our awareness.
     *
     * @param action
     */
    public void hearEvent(UAction action) {

    }

    public void walkFail(UCell cell) {
        if (this instanceof UPlayer) {
            commander.printScroll(cell.terrain().getIcon(), cell.terrain().getBonkmsg());
        }
    }

    /**
     * Can I see that thing from where I am (and I'm awake, and can see, etc)?
     *
     */
    public boolean canSee(UThing thing) {
        if (thing.area() != area()) return false;
        int x1 = areaX(); int y1 = areaY();
        int x2 = thing.areaX(); int y2 = thing.areaY();
        if (UPath.mdist(x1,y1,x2,y2) > getSightrange())
            return false;
        if (!UPath.canSee(x1,y1,x2,y2,area(),this))
            return false;
        return true;
    }

    /**
     * Can I see through this cell?
     */
    public boolean canSeeThrough(UCell cell) {
        return !cell.getTerrain().isOpaque();
    }

    public void wakeCheck(int playerx, int playery) {
        if (getLocation() == null) return;
        int dist = Math.abs(areaX() - playerx) + Math.abs(areaY() - playery);
        if (isAwake() && (getSleeprange() > 0) && (dist > getSleeprange())) {
            stopActing();
        } else if (!isAwake() && (getWakerange() > 0) && (dist < getWakerange())) {
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


    public boolean isAwake() {
        return awake;
    }

    public void setAwake(boolean awake) {
        this.awake = awake;
    }

    public int getWakerange() {
        return wakerange;
    }

    public void setWakerange(int wakerange) {
        this.wakerange = wakerange;
    }

    public int getSleeprange() {
        return sleeprange;
    }

    public void setSleeprange(int sleeprange) {
        this.sleeprange = sleeprange;
    }

    public int getSightrange() {
        return sightrange;
    }

    public void setSightrange(int sightrange) {
        this.sightrange = sightrange;
    }

    public int getCameraPinStyle() {
        return cameraPinStyle;
    }

    public void setCameraPinStyle(int cameraPinStyle) {
        this.cameraPinStyle = cameraPinStyle;
    }

    public int getMoveAnimX() {
        return moveAnimX;
    }

    public void setMoveAnimX(int moveAnimX) {
        this.moveAnimX = moveAnimX;
    }

    public int getMoveAnimY() {
        return moveAnimY;
    }

    public void setMoveAnimY(int moveAnimY) {
        this.moveAnimY = moveAnimY;
    }

    public int getMoveAnimDX() {
        return moveAnimDX;
    }

    public void setMoveAnimDX(int moveAnimDX) {
        this.moveAnimDX = moveAnimDX;
    }

    public int getMoveAnimDY() {
        return moveAnimDY;
    }

    public void setMoveAnimDY(int moveAnimDY) {
        this.moveAnimDY = moveAnimDY;
    }

    public float getActionTime() {
        return actionTime;
    }

    public void setActionTime(float actionTime) {
        this.actionTime = actionTime;
    }

    public float getActionspeed() {
        return actionspeed;
    }
    public float getMovespeed() {
        return movespeed;
    }
    public void setBodytype(String s) { bodytype = s; }
    public String getBodytype() { return bodytype; }
    public Body getBody() { return body; }
    public void setBody(Body b) { body = b; }

    public String UIstatus() {
        return "";
    }
    public UColor UIstatusColor() {
        return UColor.COLOR_GRAY;
    }
}
