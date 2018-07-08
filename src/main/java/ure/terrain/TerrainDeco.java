package ure.terrain;

import ure.areas.UCell;
import ure.math.UColor;
import ure.actors.UActor;

/**
 * TerrainDeco implements a transparent handoff to its component terrain for all UTerrain methods.
 * Extend this class to make a Decorator you can pass to TerrainCzar to intercept base UTerrain behavior.
 * You need only override the methods you want to intercept; to pass control back to those methods simply
 * call the super.method(), just as if you were extending UTerrain.
 *
 * To create differentiated subtypes of terrains, rather than affecting all base terrain behavior, you should
 * directly subclass TerrainI or its subclasses such as Door.
 */
public class TerrainDeco implements UTerrain {

    private UTerrain terrain;

    public TerrainDeco(UTerrain realTerrain) {
        /**
         * Do not override the constructor.  Do your setup in becomeReal() instead.
         */
        terrain = realTerrain;
    }

    public void initialize() {
        /**
         * Instantiate whatever needs to be derived from the json properties.
         */
        terrain.initialize();
    }

    public void becomeReal(UCell c) {
        /**
         * Configure myself once I am in a real cell in the world.  This is probably where you want
         * your setup behavior.
         * @param c The UCell we're birthing into.  Be sure to pass this down.
         */
        terrain.becomeReal(c);
    }
    public boolean isPassable() {
        /**
         * Are we passable 'by default'?
         */
        return terrain.isPassable();
    }
    public boolean isPassable(UActor actor) {
        /**
         * Are we passable to this actor right now?
         */
        return terrain.isPassable(actor);
    }
    public boolean isOpaque() {
        /**
         * Do we block light (regardless of who's looking)?
         */
        return terrain.isOpaque();
    }
    public boolean breaksLatch() {
        /**
         * Does running past us stop latched auto-player-movement?
         */
        return terrain.breaksLatch();
    }

    public char glyph() {
        /**
         * Our 'normal' glyph.
         */
        return terrain.glyph();
    }
    public char glyph(int x, int y) {
        /**
         * Our glyph at these area coordinates.  This can take PRNG variations, etc into account.
         */
        return terrain.glyph(x, y);
    }
    public int glyphOffsetX() {
        /**
         * glyph offset for realtime animation.
         */
        return terrain.glyphOffsetX(); }
    public int glyphOffsetY() {
        /**
         * glyph offset for realtime animation.
         */
        return terrain.glyphOffsetY();
    }
    public boolean glow() {
        /**
         * Do we glow our fgcolor into nearby tiles' lightcells?
         */
        return terrain.glow();
    }
    public float sunvis() {
        /**
         * How much natural sunlight can reach me?
         */
        return terrain.sunvis();
    }
    public boolean preventMoveFrom(UActor actor) {
        /**
         * Do I prevent actor from moving out of me?
         */
        return terrain.preventMoveFrom(actor);
    }
    public float moveSpeed(UActor actor) {
        /**
         * How fast can actor move into me?  (higher is faster, 1.0f is standard)
         */
        return terrain.moveSpeed(actor);
    }
    public void moveTriggerFrom(UActor actor, UCell cell) {
        /**
         * Actor attempted to walk into me (in cell).  I should either move her here, or tell her she failed.
         */
        terrain.moveTriggerFrom(actor, cell);
    }
    public void walkedOnBy(UActor actor, UCell cell) {
        /**
         * Actor walked into me (in cell).  I should print messages, apply effects, fire triggers, etc.
         */
        terrain.walkedOnBy(actor, cell);
    }

    public void printScroll(String msg, UCell cell) { terrain.printScroll(msg, cell); }

    public void animationTick() {
        /**
         * Update our realtime animations.
         */
        terrain.animationTick();
    }

    public UColor bgColor() { return terrain.bgColor(); }
    public UColor bgColorBuffer() {
        /**
         * DNO
         */
        return terrain.bgColorBuffer(); }
    public UColor fgColor() { return terrain.fgColor(); }
    public UColor fgColorBuffer() {
        /**
         * DNO
         */
        return terrain.fgColorBuffer(); }
    public String name() {
        /**
         * WARNING: URE uses the name to look up terrains internally.  Overriding this could make your system
         * more fragile.  Think about what you're doing.
         */
        return terrain.name(); }
    public String bonkmsg() { return terrain.bonkmsg(); }

}
