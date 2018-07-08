package ure.terrain;

import ure.areas.UCell;
import ure.math.UColor;
import ure.actors.UActor;

public interface UTerrain {
    boolean isPassable();
    boolean isPassable(UActor actor);
    boolean isOpaque();
    void initialize();
    void becomeReal(UCell c);
    char glyph();
    char glyph(int x, int y);
    int glyphOffsetX();
    int glyphOffsetY();
    void moveTriggerFrom(UActor actor, UCell cell);
    boolean preventMoveFrom(UActor actor);
    float moveSpeed(UActor actor);
    void walkedOnBy(UActor actor, UCell cell);
    void printScroll(String msg, UCell cell);
    float sunvis();
    void animationTick();
    boolean glow();
    UColor bgColor();
    UColor bgColorBuffer();
    UColor fgColor();
    UColor fgColorBuffer();
    String name();
    String bonkmsg();
}
