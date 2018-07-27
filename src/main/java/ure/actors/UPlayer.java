package ure.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.areas.UCell;
import ure.math.UColor;
import ure.areas.UArea;
import ure.ui.ULight;
import ure.things.UThing;

/**
 * UPlayer implements a UActor whose actions are initiated by user input.  More than one can exist.
 *
 */
public class UPlayer extends UActor {

    public static final String TYPE = "player";

    public boolean awake = true;

    public String saveAreaLabel;
    public int saveAreaX;
    public int saveAreaY;
    public int saveTurn;
    public UColor selfLightColor;
    public int selfLight;
    public int selfLightFalloff;

    @JsonIgnore
    ULight light;

    public UPlayer() {
        super();
    }

    public UPlayer(String thename, char theicon, UColor thecolor, boolean addOutline, UColor selfLightColor, int selfLight, int selfLightFalloff) {
        super();
        initialize();
        this.selfLight = selfLight;
        this.selfLightFalloff = selfLightFalloff;
        this.selfLightColor = selfLightColor;
        setDisplayFields(thename, theicon, thecolor, addOutline);
        if (selfLight > 0) {
            light = new ULight(selfLightColor, selfLightFalloff + selfLight, selfLight);
        }
    }

    @Override
    public void addActionTime(float v) {
        System.out.println("adding action time to player");
        super.addActionTime(v);
    }

    public void reconnectThings() {
        for (UThing thing : contents.getThings()) {
            thing.setLocation(this);
        }
        if (selfLight > 0) {
            light = new ULight(selfLightColor, selfLightFalloff + selfLight, selfLight);
        }
    }

    @Override
    public void moveToCell(UArea area, int destX, int destY) {
        super.moveToCell(area,destX,destY);
        if (light != null) {
            light.moveTo(area,destX,destY);
        }
        if (area.terrainAt(destX,destY).isBreaklatch())
            commander.latchBreak();

    }

    /** Remove ourselves from the area into nowhere, to get ready for game terminate
     * Normal actors don't have to do this, but Player does because she prevents the area from freezing.
     */
    public void prepareToVanish() {
        commander.unregisterActor(this);
        location.removeThing(this);
    }

    @Override
    public boolean canSee(UThing thing) {
        int x = thing.areaX();
        int y = thing.areaY();
        if (camera.visibilityAt(x - camera.leftEdge, y - camera.topEdge) > commander.config.getVisibilityThreshold())
            if (camera.lightAt(x-camera.leftEdge,y-camera.topEdge).grayscale() > commander.config.getVisibilityThreshold())
            return true;
        return false;
    }

    @Override
    public void walkFail(UCell cell) {
        super.walkFail(cell);
        commander.latchBreak();
    }

    @Override
    public int bounceAnimY() {
        return 0;
    }

    @Override
    public void closeOut() {

    }

    public void saveStateData() {
        saveAreaLabel = area().getLabel();
        saveAreaX = areaX();
        saveAreaY = areaY();
        saveTurn = commander.getTurn();
    }

    public void setSaveAreaLabel(String l) { saveAreaLabel = l; }
    public String getSaveAreaLabel() { return saveAreaLabel; }
    public void setSaveAreaX(int _x) { saveAreaX = _x; }
    public void setSaveAreaY(int _y) { saveAreaY = _y; }
    public int getSaveAreaX() { return saveAreaX; }
    public int getSaveAreaY() { return saveAreaY; }
    public int getSaveTurn() { return saveTurn; }
    public void setSaveTurn(int s) { saveTurn = s; }
    public UColor getSelfLightColor() { return selfLightColor; }
    public int getSelfLight() { return selfLight; }
    public int getSelfLightFalloff() { return selfLightFalloff; }
    public void setSelfLightColor(UColor c) { selfLightColor = c; }
    public void setSelfLight(int c) { selfLight = c; }
    public void setSelfLightFalloff(int c) { selfLightFalloff = c; }

}
