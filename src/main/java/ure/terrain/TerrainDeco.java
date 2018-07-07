package ure.terrain;

import ure.UCell;
import ure.math.UColor;
import ure.actors.UActor;

public class TerrainDeco implements UTerrain {

    private UTerrain terrain;

    public TerrainDeco(UTerrain realTerrain) {
        terrain = realTerrain;
    }

    public boolean isPassable() { return terrain.isPassable(); }
    public boolean isPassable(UActor actor) { return terrain.isPassable(actor); }
    public boolean isOpaque() { return terrain.isOpaque(); }
    public void initialize() { terrain.initialize(); }
    public void becomeReal(UCell c) { terrain.becomeReal(c); }
    public char glyph() { return terrain.glyph(); }
    public char glyph(int x, int y) { return terrain.glyph(x, y); }
    public int glyphOffsetX() { return terrain.glyphOffsetX(); }
    public int glyphOffsetY() { return terrain.glyphOffsetY(); }
    public void moveTriggerFrom(UActor actor, UCell cell) { terrain.moveTriggerFrom(actor, cell); }
    public boolean preventMoveFrom(UActor actor) { return terrain.preventMoveFrom(actor); }
    public float moveSpeed(UActor actor) { return terrain.moveSpeed(actor); }
    public void walkedOnBy(UActor actor, UCell cell) { terrain.walkedOnBy(actor, cell); }
    public void printScroll(String msg, UCell cell) { terrain.printScroll(msg, cell); }
    public float sunvis() { return terrain.sunvis(); }
    public void animationTick() { terrain.animationTick(); }
    public boolean glow() { return terrain.glow(); }
    public UColor bgColor() { return terrain.bgColor(); }
    public UColor bgColorBuffer() { return terrain.bgColorBuffer(); }
    public UColor fgColor() { return terrain.fgColor(); }
    public UColor fgColorBuffer() { return terrain.fgColorBuffer(); }
    public String name() { return terrain.name(); }
}
