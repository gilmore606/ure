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
    /**
     * Do not override the constructor.  Do your setup in becomeReal() instead.
     */
    public TerrainDeco(UTerrain realTerrain) {

        terrain = realTerrain;
    }
    /**
     * Instantiate whatever needs to be derived from the json properties.
     */
    public void initialize() {

        terrain.initialize();
    }
    /**
     * Configure myself once I am in a real cell in the world.  This is probably where you want
     * your setup behavior.
     * @param c The UCell we're birthing into.  Be sure to pass this down.
     */
    public void becomeReal(UCell c) {

        terrain.becomeReal(c);
    }
    /**
     * Are we passable 'by default'?
     */
    public boolean isPassable() {

        return terrain.isPassable();
    }
    /**
     * Are we passable to this actor right now?
     */
    public boolean isPassable(UActor actor) {

        return terrain.isPassable(actor);
    }
    /**
     * Do we block light (regardless of who's looking)?
     */
    public boolean isOpaque() {

        return terrain.isOpaque();
    }
    /**
     * Does running past us stop latched auto-player-movement?
     */
    public boolean breaksLatch() {

        return terrain.breaksLatch();
    }
    /**
     * Our 'normal' glyph.
     */
    public char glyph() {

        return terrain.glyph();
    }
    /**
     * Our glyph at these area coordinates.  This can take PRNG variations, etc into account.
     */
    public char glyph(int x, int y) {

        return terrain.glyph(x, y);
    }
    /**
     * glyph offset for realtime animation.
     */
    public int glyphOffsetX() {

        return terrain.glyphOffsetX();
    }
    /**
     * glyph offset for realtime animation.
     */
    public int glyphOffsetY() {

        return terrain.glyphOffsetY();
    }
    /**
     * Do we glow our fgcolor into nearby tiles' lightcells?
     */
    public boolean glow() {

        return terrain.glow();
    }
    /**
     * How much natural sunlight can reach me?
     */
    public float sunvis() {

        return terrain.sunvis();
    }
    /**
     * Do I prevent actor from moving out of me?
     */
    public boolean preventMoveFrom(UActor actor) {

        return terrain.preventMoveFrom(actor);
    }
    /**
     * How fast can actor move into me?  (higher is faster, 1.0f is standard)
     */
    public float moveSpeed(UActor actor) {

        return terrain.moveSpeed(actor);
    }
    /**
     * Actor attempted to walk into me (in cell).  I should either move her here, or tell her she failed.
     */
    public void moveTriggerFrom(UActor actor, UCell cell) {

        terrain.moveTriggerFrom(actor, cell);
    }
    /**
     * Actor walked into me (in cell).  I should print messages, apply effects, fire triggers, etc.
     */
    public void walkedOnBy(UActor actor, UCell cell) {

        terrain.walkedOnBy(actor, cell);
    }

    public void printScroll(String msg, UCell cell) { terrain.printScroll(msg, cell); }
    /**
     * Update our realtime animations.
     */
    public void animationTick() {

        terrain.animationTick();
    }

    public UColor bgColor() { return terrain.bgColor(); }
    /**
     * DNO
     */
    public UColor bgColorBuffer() {

        return terrain.bgColorBuffer(); }
    public UColor fgColor() { return terrain.fgColor(); }
    /**
     * DNO
     */
    public UColor fgColorBuffer() {

        return terrain.fgColorBuffer(); }
    /**
     * WARNING: URE uses the name to look up terrains internally.  Overriding this could make your system
     * more fragile.  Think about what you're doing.
     */
    public String name() {

        return terrain.name();
    }
    public String bonkmsg() { return terrain.bonkmsg(); }

}
