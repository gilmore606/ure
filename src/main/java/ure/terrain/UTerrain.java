package ure.terrain;

import ure.actions.Interactable;
import ure.areas.UCell;
import ure.math.UColor;
import ure.actors.UActor;
import ure.ui.Icon;

/**
 * UTerrain defines all the public methods of terrain.  It should not be implemented directly to create new custom terrain.
 * Instead, TerrainDeco should be extended.
 */
public interface UTerrain {
    boolean isPassable();
    boolean isPassable(UActor actor);
    boolean isOpaque();
    boolean breaksLatch();
    void initialize();
    void becomeReal(UCell c);
    char getGlyph();
    char glyph(int x, int y);
    Icon getIcon();
    String[] UIdetails(String context);
    int glyphOffsetX();
    int glyphOffsetY();
    void moveTriggerFrom(UActor actor, UCell cell);
    boolean preventMoveFrom(UActor actor);
    float moveSpeed(UActor actor);
    void walkedOnBy(UActor actor, UCell cell);
    boolean isInteractable(UActor actor);
    float interactionFrom(UActor actor);
    void printScroll(String msg, UCell cell);
    float sunvis();
    void animationTick();
    boolean glow();
    UColor bgColor();
    UColor bgColorBuffer();
    UColor fgColor();
    UColor fgColorBuffer();
    String getName();
    String getPlural();
    String bonkmsg();
    int getStat(String stat);
    void setStat(String stat, int value);
}
