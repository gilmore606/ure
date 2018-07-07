package ure.terrain;

import ure.UCell;
import ure.UColor;
import ure.actors.UREActor;

public interface URETerrain {
    boolean isPassable();
    boolean isPassable(UREActor actor);
    boolean isOpaque();
    void initialize();
    void becomeReal(UCell c);
    char glyph();
    char glyph(int x, int y);
    int glyphOffsetX();
    int glyphOffsetY();
    void moveTriggerFrom(UREActor actor, UCell cell);
    boolean preventMoveFrom(UREActor actor);
    float moveSpeed(UREActor actor);
    void walkedOnBy(UREActor actor, UCell cell);
    void printScroll(String msg, UCell cell);
    float sunvis();
    void animationTick();
    boolean glow();
    UColor bgColor();
    UColor bgColorBuffer();
    UColor fgColor();
    UColor fgColorBuffer();
    String name();
}
