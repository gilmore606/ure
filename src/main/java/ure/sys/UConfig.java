package ure.sys;

import ure.areas.UArea;
import ure.math.UColor;
import ure.ui.UCamera;

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
    public static int SHADOW_FULLDIM = 3;        // dim entire camera (or window) behind modals (using shadowcolor)

    public static int POS_CAMERA_CENTER = 0;     // modals centered in camera pane
    public static int POS_WINDOW_CENTER = 1;     // modals centered in window


    // Visuals

    private int FPStarget = 30;                                 // render at this FPS
    private int animFrameMilliseconds = 33;                     // milliseconds between animation frames
    private int screenWidth = 1400;                             // window width in pixels
    private int screenHeight = 1000;                            // window height in pixels
    private String tileFont = "/fonts/Deferral-Square.ttf";    // irrelevant for non-ascii renderer
    private float tileFontSize = 20;
    private String textFont = "/fonts/UbuntuMono-R.ttf";
    private float textFontSize = 18;
    private int tileWidth = 20;
    private int tileHeight = 20;
    private int textWidth = 10;
    private int textHeight = 18;

    private String uiCheckGlyph = "*";                          // checkmark for UI selections
    private String uiFrameGlyphs;                  // corners and sides for UI box frames
    private int modalFrameLine = 3;                             // thickness of pixel line around modals
    private int modalShadowStyle = UConfig.SHADOW_BLOCK;        // drop shadow style for modal popups
    private int modalPosition = UConfig.POS_CAMERA_CENTER;      // position of modal popups
    private boolean wrapSelect = true;                          // wrap around when scrolling through selections
    private int cursorBlinkSpeed = 20;

    private UColor windowBgColor = UColor.COLOR_BLACK;                          // bgColor of game window
    private UColor cameraBgColor = UColor.COLOR_BLACK;                          // bgColor of camera (for unseen territory)
    private UColor modalBgColor = new UColor(0.1f,0.1f,0f);                         // bgColor of modal popups
    private UColor modalFrameColor = new UColor(0.7f,0.7f,0.1f);     // glyph color for modal popup frame glyphs
    private UColor modalShadowColor = new UColor(0f,0f,0f,0.5f);  // color (and alpha) of modal shadows
    private UColor textColor = UColor.COLOR_OFFWHITE;                             // color for ui/scroll text
    private UColor hiliteColor = new UColor(1f,1f,0.2f, 0.3f);            // color for ui selection highlighting

    private boolean outlineActors = true;                           // draw a black outline around Actor glyphs?
    private boolean outlineThings = false;                          // draw a black outline around Thing glyphs?

    private int moveAnimFrames = 4;                     // how many frames to animate actor movement?
    private int moveAnimPlayerFrames = 0;               // how many frames to animate player movement?
    private float actorBounceSpeed = 1.6f;                // how fast to make actors animate-bounce?
    private float actorBounceAmount = 0.8f;               // how much to make actors animate-bounce?

    private int cameraPinStyle = UCamera.PINSTYLE_SOFT; // default pinstyle for player camera
    private boolean visibilityEnable = true;            // if false, assume everything is visible (no occlusion)
    private boolean lightEnable = true;                 // if false, assume all areas lit 100%
    private boolean lightBloom = true;                  // TODO: lights adding to >fullbright bloom to white
    private boolean smoothLightCones = true;            // dither edges of light cones

    private float visibilityThreshold = 0.2f;           // how 'visible' is a cell before we consider it seen? (ucamera 512, 537)
    private float seenOpacity = 0.55f;                  // how bright to draw seen-but-not-visible terrain
    private float seenSaturation = 0.07f;               // how much color to leave in seen-but-not-visible terrain
    private boolean seenLightGray = true;               // ignore light in seen-but-not-visible and use gray
    private float lightHueToFloors = 0.8f;              // TODO: how much color lights give to terrain
    private float lightHueToWalls = 0.8f;               // TODO: how much color lights give to walls
    private float lightHueToThings = 0.5f;              // TODO: how much color lights give to things
    private float lightHueToActors = 0.4f;              // TODO: how much color lights give to actors

    private ArrayList<UColor> sunColorLerps;            // lerp points for sunlight color cycle
    private ArrayList<Integer> sunColorLerpMarkers;
    private HashMap<Integer,String> sunCycleMessages;

    // Audio

    private float volumeMaster = 1f;
    private float volumeMusic = 0.2f;
    private float volumeAmbient = 1f;
    private float volumeWorld = 1f;
    private float volumeUI = 0.5f;
    private float musicFadeTime = 2f;                   // seconds to crossfade background music

    private int volumeFalloffDistance = 25;             // cells away for a sound to attenuate to -infDB
    private String titleMusic = "sounds/ultima_towns.ogg";

    public String soundUImodalOpen = "sounds/echo_alert_rev.ogg";
    public String soundUIcursorUp = "sounds/mouse_over3.wav";
    public String soundUIcursorDown = "sounds/mouse_over3.wav";
    public String soundUIselectClose = "sounds/melodic2_click.ogg";
    public String soundUIcancelClose = "sounds/echo_alert.ogg";
    public String soundUIkeystroke = "sounds/mouse_over3.ogg";
    public String soundUIbumpLimit = "sounds/melodic1_click.ogg";

    // Game functionality

    private String resourcePath = "src/main/resources/";     // path to resource files
    private String savePath = "saves/";                      // path to game save files

    private boolean backgroundLoader = true;        // load and save areas in the background?
    private boolean persistentAreas = true;         // persist and recover generated areas?
    private boolean runNeighborAreas = true;        // keep areas we just left awake?
    private boolean loadAreasAhead = true;          // preemptively load upcoming areas?

    private boolean nethackShiftRun = true;        // nethack-style Shift+Move?  false for 'shift = no repeat wait' simpler running
    private boolean smartInteract = true;           // autodetect targets for interact command?
    private boolean interactStairs = true;          // interact command can trigger stairs?

    private int turnsPerDay = 512;                  // game turns per 24 hour day
    private int dayTimeStartOffset = 300;           // game turns to add at game start to get the 'starting daytime'


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
        area.setSunColorLerps(sunColorLerps);
        area.setSunColorLerpMarkers(sunColorLerpMarkers);
        area.setSunCycleMessages(sunCycleMessages);
    }

    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
    public void setScreenWidth(int screenWidth) { this.screenWidth = screenWidth; }
    public void setScreenHeight(int screenHeight) { this.screenHeight = screenHeight; }

    public int getMoveAnimPlayerFrames() {
        return moveAnimPlayerFrames;
    }

    public void setMoveAnimPlayerFrames(int moveAnimPlayerFrames) {
        this.moveAnimPlayerFrames = moveAnimPlayerFrames;
    }
    public int getDayTimeStartOffset() {
        return dayTimeStartOffset;
    }

    public void setDayTimeStartOffset(int dayTimeStartOffset) {
        this.dayTimeStartOffset = dayTimeStartOffset;
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

    public String getTileFont() {
        return tileFont;
    }

    public void setTileFont(String tileFont) {
        this.tileFont = tileFont;
    }

    public String getTextFont() {
        return textFont;
    }

    public void setTextFont(String textFont) {
        this.textFont = textFont;
    }

    public float getTileFontSize() {
        return tileFontSize;
    }

    public void setTileFontSize(float tileFontSize) {
        this.tileFontSize = tileFontSize;
    }

    public float getTextFontSize() {
        return textFontSize;
    }

    public void setTextFontSize(float textFontSize) {
        this.textFontSize = textFontSize;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public void setTextHeight(int textHeight) {
        this.textHeight = textHeight;
    }

    public String getUiCheckGlyph() { return uiCheckGlyph; }
    public void setUiCheckGlyph(String s) { uiCheckGlyph = s; }

    public String getUiFrameGlyphs() {
        return uiFrameGlyphs;
    }
    public void setUiFrameGlyphs(String uiFrameGlyphs) {
        this.uiFrameGlyphs = uiFrameGlyphs;
    }

    public int getModalFrameLine() { return modalFrameLine; }
    public void setModalFrameLine(int thickness) { modalFrameLine = thickness; }

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

    public boolean isWrapSelect() { return wrapSelect; }
    public void setWrapSelect(boolean wrap) { wrapSelect = wrap; }

    public int getCursorBlinkSpeed() { return cursorBlinkSpeed; }

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

    public UColor getModalShadowColor() { return modalShadowColor; }
    public void setModalShadowColor(UColor modalShadowColor) { this.modalShadowColor = modalShadowColor; }

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

    public int getCameraPinStyle() { return cameraPinStyle; }
    public void setCameraPinStyle(int p) { cameraPinStyle = p; }

    public boolean isVisibilityEnable() {
        return visibilityEnable;
    }

    public void setVisibilityEnable(boolean visibilityEnable) {
        this.visibilityEnable = visibilityEnable;
    }

    public int getMoveAnimFrames() { return moveAnimFrames; }

    public void setMoveAnimFrames(int moveAnimFrames) { this.moveAnimFrames = moveAnimFrames; }

    public float getActorBounceSpeed() { return actorBounceSpeed; }

    public void setActorBounceSpeed(float actorBounceSpeed) { this.actorBounceSpeed = actorBounceSpeed; }

    public float getActorBounceAmount() { return actorBounceAmount; }

    public void setActorBounceAmount(float actorBounceAmount) { this.actorBounceAmount = actorBounceAmount; }

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

    public float getSeenSaturation() {
        return seenSaturation;
    }

    public void setSeenSaturation(float seenSaturation) {
        this.seenSaturation = seenSaturation;
    }

    public void setSeenLightGray(boolean t) { seenLightGray = t; }
    public boolean isSeenLightGray() { return seenLightGray; }

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

    public String getResourcePath() { return resourcePath; }
    public void setResourcePath(String path) { resourcePath = path; }
    public String getSavePath() { return savePath; }
    public void setSavePath(String path) { savePath = path; }

    public boolean isPersistentAreas() {
        return persistentAreas;
    }

    public void setPersistentAreas(boolean persistentAreas) {
        this.persistentAreas = persistentAreas;
    }

    public boolean isBackgroundLoader() { return backgroundLoader; }
    public void setBackgroundLoader(boolean value) { backgroundLoader = value; }

    public boolean isRunNeighborAreas() {
        return runNeighborAreas;
    }

    public void setRunNeighborAreas(boolean runNeighborAreas) {
        this.runNeighborAreas = runNeighborAreas;
    }

    public boolean isLoadAreasAhead() { return loadAreasAhead; }
    public void setLoadAreasAhead(boolean loadAreasAhead) { this.loadAreasAhead = loadAreasAhead; }

    public boolean isSmartInteract() {
        return smartInteract;
    }

    public void setSmartInteract(boolean smartInteract) {
        this.smartInteract = smartInteract;
    }

    public boolean isNethackShiftRun() { return nethackShiftRun; }
    public void setNethackShiftRun(boolean b) { nethackShiftRun = b; }

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

    public void setVolumeMaster(float v) { volumeMaster = v; }
    public void setVolumeMusic(float v) { volumeMusic = v; }
    public void setVolumeAmbient(float v) { volumeAmbient = v; }
    public void setVolumeWorld(float v) { volumeWorld = v; }
    public void setVolumeUI(float v) { volumeUI = v; }
    public float getVolumeMaster() { return volumeMaster; }
    public float getVolumeMusic() { return volumeMusic; }
    public float getVolumeAmbient() { return volumeAmbient; }
    public float getVolumeWorld() { return volumeWorld; }
    public float getVolumeUI() { return volumeUI; }
    public float getMusicFadeTime() { return musicFadeTime; }
    public void setMusicFadeTime(float f) { musicFadeTime = f; }
    public String getTitleMusic() { return titleMusic; }
    public void setTitleMusic(String s) { titleMusic = s; }
    public void setVolumeFalloffDistance(int d) { volumeFalloffDistance = d; }
    public int getVolumeFalloffDistance() { return volumeFalloffDistance; }
}
