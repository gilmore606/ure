package ure.sys;

import ure.areas.UArea;
import ure.math.UColor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * UConfig implements a singleton to hold global configuration parameters of the engine.  Other classes also have custom configuration,
 * but values which affect global behavior, or several different engine elements, or are simply more convenient to have centralized
 * will be found here.
 *
 * The UCommander makes a UConfig on startup; you don't need to make one in your game code.  To set its values simply call the
 * setter methods on commander.config in your game's startup sequence.
 *
 */
public class UConfig {

    public static int SHADOW_NONE = 0;           // no dropshadow on modals
    public static int SHADOW_BLOCK = 1;          // 1-cell constant dropshadow on modals
    public static int SHADOW_GRADIENT = 2;       // 1-cell gradient dropshadow on modals
    public static int SHADOW_FULLDIM = 3;        // dim entire camera (or window) behind modals

    public static int POS_CAMERA_CENTER = 0;     // modals centered in camera pane
    public static int POS_WINDOW_CENTER = 1;     // modals centered in window


    // Visuals

    private int FPStarget = 60;                                 // render at this FPS
    private int animFrameMilliseconds = 33;                     // milliseconds between animation frames

    private String glyphFont = "Deferral-Square.ttf";               // irrelevant for non-ascii renderer
    private String textFont = "Px437_Phoenix_BIOS-2y.ttf";

    private int glyphWidth = 16;
    private int glyphHeight = 17;

    private String uiFrameGlyphs = "+=+|+=|";                               // corners and sides for UI box frames
    private int modalShadowStyle = UConfig.SHADOW_GRADIENT;     // drop shadow style for modal popups
    private int modalPosition = UConfig.POS_CAMERA_CENTER;      // position of modal popups

    private UColor windowBgColor = UColor.COLOR_BLACK;                          // bgColor of game window
    private UColor cameraBgColor = UColor.COLOR_BLACK;                          // bgColor of camera (for unseen territory)
    private UColor modalBgColor = UColor.COLOR_BLACK;                           // bgColor of modal popups
    private UColor modalFrameColor = new UColor(0.7f,0.7f,0.1f);     // glyph color for modal popup frame glyphs
    private UColor textColor = UColor.COLOR_WHITE;                             // color for ui/scroll text
    private UColor hiliteColor = new UColor(1f,1f,0.2f);            // color for ui selection highlighting

    private boolean outlineActors = true;                           // draw a black outline around Actor glyphs?
    private boolean outlineThings = false;                          // draw a black outline around Thing glyphs?

    private boolean visibilityEnable = true;            // if false, assume everything is visible (no occlusion)
    private boolean lightEnable = true;                 // if false, assume all areas lit 100%
    private boolean lightBloom = true;                  // TODO: lights adding to >fullbright bloom to white
    private boolean smoothLightCones = true;            // dither edges of light cones (line 339 in UCamera)

    private float visibilityThreshold = 0.3f;           // how 'visible' is a cell before we consider it seen? (ucamera 512, 537)
    private float seenOpacity = 0.35f;                  // how bright to draw seen-but-not-visible terrain
    private float seenHue = 0.3f;                       // TODO: how much color to leave in seen-but-not-visible terrain
    private float lightHueToFloors = 0.8f;              // TODO: how much color lights give to terrain
    private float lightHueToWalls = 0.8f;               // TODO: how much color lights give to walls
    private float lightHueToThings = 0.5f;              // TODO: how much color lights give to things
    private float lightHueToActors = 0.4f;              // TODO: how much color lights give to actors

    private ArrayList<UColor> sunColorLerps;            // lerp points for sunlight color cycle
    private ArrayList<Integer> sunColorLerpMarkers;
    private HashMap<Integer,String> sunCycleMessages;

    // Game functionality

    private boolean persistentAreas = true;         // persist and recover generated areas?
    private boolean runNeighborAreas = true;        // TODO: keep areas adjacent to current area awake?

    private boolean smartInteract = true;           // autodetect targets for interact command?
    private boolean interactStairs = true;          // interact command can trigger stairs?

    private int turnsPerDay = 512;                  // game turns per 24 hour day



    public UConfig() {
        clearSunLerps();
        // A default sunrise-sunset.  Probably fine for most normal earthlike worlds.
        AddSunLerp(0,     0.1f, 0.1f, 0.1f, "");
        AddSunLerp(4*60,  0.1f, 0.1f, 0.3f, "");
        AddSunLerp(6*60,  0.7f, 0.7f, 0.45f, "The sun's first rays appear on the horizon.");
        AddSunLerp(9*60,  1f, 0.9f, 0.75f, "");
        AddSunLerp(13*60, 1f, 1f, 1f, "");
        AddSunLerp(17*60, 0.9f, 0.9f, 1f, "");
        AddSunLerp(19*60, 0.8f, 0.7f, 0.8f, "");
        AddSunLerp(20*60, 0.9f, 0.4f, 0.4f, "");
        AddSunLerp(21*60, 0.4f, 0.3f, 0.4f, "The sun sets.");
        AddSunLerp(24*60, 0.1f, 0.1f, 0.3f, "");
    }

    /**
     * To customize the global sunlight color cycle, first clearSunLerps() to remove the default cycle.
     *
     */
    public void clearSunLerps() {
        sunColorLerps = new ArrayList<UColor>();
        sunColorLerpMarkers = new ArrayList<Integer>();
        sunCycleMessages = new HashMap<Integer,String>();
    }

    /**
     * Add a single lerp point to the sunlight cycle.  Call this repeatedly to continue adding points.
     * Be sure you cover the entire 24 hour cycle or you will cause a crash.
     *
     * @param min The number of minutes from midnight at which to hit this color value.
     * @param r
     * @param g
     * @param b
     * @param msg A scroll message to print when this time is reached (optional).
     */
    public void AddSunLerp(int min, float r, float g, float b, String msg) {
        sunColorLerps.add(new UColor(r,g,b));
        sunColorLerpMarkers.add(min);
        if (msg != null)
            sunCycleMessages.put((Integer)min, msg);
    }

    /**
     * Add the default sunlight color cycle stored on UConfig to the given area.
     *
     * @param area
     */
    public void addDefaultSunCycle(UArea area) {
        area.sunColorLerps = sunColorLerps;
        area.sunColorLerpMarkers = sunColorLerpMarkers;
        area.sunCycleMessages = sunCycleMessages;
    }

    public int getFPStarget() {
        return FPStarget;
    }

    public void setFPStarget(int FPStarget) {
        this.FPStarget = FPStarget;
    }

    public int getAnimFrameMilliseconds() {
        return animFrameMilliseconds;
    }

    public void setAnimFrameMilliseconds(int animFrameMilliseconds) {
        this.animFrameMilliseconds = animFrameMilliseconds;
    }

    public String getGlyphFont() {
        return glyphFont;
    }

    public void setGlyphFont(String glyphFont) {
        this.glyphFont = glyphFont;
    }

    public String getTextFont() {
        return textFont;
    }

    public void setTextFont(String textFont) {
        this.textFont = textFont;
    }

    public int getGlyphWidth() {
        return glyphWidth;
    }

    public void setGlyphWidth(int glyphWidth) {
        this.glyphWidth = glyphWidth;
    }

    public int getGlyphHeight() {
        return glyphHeight;
    }

    public void setGlyphHeight(int glyphHeight) {
        this.glyphHeight = glyphHeight;
    }

    public String getUiFrameGlyphs() {
        return uiFrameGlyphs;
    }

    public void setUiFrameGlyphs(String uiFrameGlyphs) {
        this.uiFrameGlyphs = uiFrameGlyphs;
    }

    public int getModalShadowStyle() {
        return modalShadowStyle;
    }

    public void setModalShadowStyle(int modalShadowStyle) {
        this.modalShadowStyle = modalShadowStyle;
    }

    public int getModalPosition() {
        return modalPosition;
    }

    public void setModalPosition(int modalPosition) {
        this.modalPosition = modalPosition;
    }

    public UColor getWindowBgColor() {
        return windowBgColor;
    }

    public void setWindowBgColor(UColor windowBgColor) {
        this.windowBgColor = windowBgColor;
    }

    public UColor getCameraBgColor() {
        return cameraBgColor;
    }

    public void setCameraBgColor(UColor cameraBgColor) {
        this.cameraBgColor = cameraBgColor;
    }

    public UColor getModalBgColor() {
        return modalBgColor;
    }

    public void setModalBgColor(UColor modalBgColor) {
        this.modalBgColor = modalBgColor;
    }

    public UColor getModalFrameColor() {
        return modalFrameColor;
    }

    public void setModalFrameColor(UColor modalFrameColor) {
        this.modalFrameColor = modalFrameColor;
    }

    public UColor getTextColor() {
        return textColor;
    }

    public void setTextColor(UColor textColor) {
        this.textColor = textColor;
    }

    public UColor getHiliteColor() {
        return hiliteColor;
    }

    public void setHiliteColor(UColor hiliteColor) {
        this.hiliteColor = hiliteColor;
    }

    public boolean isOutlineActors() {
        return outlineActors;
    }

    public void setOutlineActors(boolean outlineActors) {
        this.outlineActors = outlineActors;
    }

    public boolean isOutlineThings() {
        return outlineThings;
    }

    public void setOutlineThings(boolean outlineThings) {
        this.outlineThings = outlineThings;
    }

    public boolean isVisibilityEnable() {
        return visibilityEnable;
    }

    public void setVisibilityEnable(boolean visibilityEnable) {
        this.visibilityEnable = visibilityEnable;
    }

    public boolean isLightEnable() {
        return lightEnable;
    }

    public void setLightEnable(boolean lightEnable) {
        this.lightEnable = lightEnable;
    }

    public boolean isLightBloom() {
        return lightBloom;
    }

    public void setLightBloom(boolean lightBloom) {
        this.lightBloom = lightBloom;
    }

    public boolean isSmoothLightCones() {
        return smoothLightCones;
    }

    public void setSmoothLightCones(boolean smoothLightCones) {
        this.smoothLightCones = smoothLightCones;
    }

    public float getVisibilityThreshold() {
        return visibilityThreshold;
    }

    public void setVisibilityThreshold(float visibilityThreshold) {
        this.visibilityThreshold = visibilityThreshold;
    }

    public float getSeenOpacity() {
        return seenOpacity;
    }

    public void setSeenOpacity(float seenOpacity) {
        this.seenOpacity = seenOpacity;
    }

    public float getSeenHue() {
        return seenHue;
    }

    public void setSeenHue(float seenHue) {
        this.seenHue = seenHue;
    }

    public float getLightHueToFloors() {
        return lightHueToFloors;
    }

    public void setLightHueToFloors(float lightHueToFloors) {
        this.lightHueToFloors = lightHueToFloors;
    }

    public float getLightHueToWalls() {
        return lightHueToWalls;
    }

    public void setLightHueToWalls(float lightHueToWalls) {
        this.lightHueToWalls = lightHueToWalls;
    }

    public float getLightHueToThings() {
        return lightHueToThings;
    }

    public void setLightHueToThings(float lightHueToThings) {
        this.lightHueToThings = lightHueToThings;
    }

    public float getLightHueToActors() {
        return lightHueToActors;
    }

    public void setLightHueToActors(float lightHueToActors) {
        this.lightHueToActors = lightHueToActors;
    }

    public boolean isPersistentAreas() {
        return persistentAreas;
    }

    public void setPersistentAreas(boolean persistentAreas) {
        this.persistentAreas = persistentAreas;
    }

    public boolean isRunNeighborAreas() {
        return runNeighborAreas;
    }

    public void setRunNeighborAreas(boolean runNeighborAreas) {
        this.runNeighborAreas = runNeighborAreas;
    }

    public boolean isSmartInteract() {
        return smartInteract;
    }

    public void setSmartInteract(boolean smartInteract) {
        this.smartInteract = smartInteract;
    }

    public boolean isInteractStairs() {
        return interactStairs;
    }

    public void setInteractStairs(boolean interactStairs) {
        this.interactStairs = interactStairs;
    }

    public int getTurnsPerDay() {
        return turnsPerDay;
    }

    public void setTurnsPerDay(int turnsPerDay) {
        this.turnsPerDay = turnsPerDay;
    }
}
