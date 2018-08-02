package ure.terrain;

import ure.actors.UActor;
import ure.areas.UCell;
import ure.math.UColor;
import ure.ui.particles.ParticleSplash;
import ure.ui.particles.UParticle;

/**
 * Water has a pretty animation.  It should probably do something more interesting.
 *
 */
public class Water extends UTerrain {

    public static final String TYPE = "water";

    public int shimmerFrames = 80;
    public int shimmerVariance = 50;

    protected int waves;

    @Override
    public int glyphOffsetY() {
        int f = getAnimationFrame();
        f = (f + cell.areaX() * 10 + cell.areaY()) % getAnimationFrames();
        int mid = getAnimationFrames() /2;
        if (f > mid + 1)
            f =  getAnimationFrames() - f;
        float n = (float)f / (float)(getAnimationFrames() / 2);
        n = n * 2;
        return (int)(Math.sin((double)n * 6.28) * waves - waves);
    }

    public int getWaves() {
        return waves;
    }

    public void setWaves(int waves) {
        this.waves = waves;
    }

    @Override
    public void animationTick() {
        super.animationTick();
        if (animationFrame % (shimmerFrames + commander.random.nextInt(shimmerVariance)) == 0) {
            applyColorVariance();
        }
    }

    @Override
    public void walkedOnBy(UActor actor, UCell cell) {
        cell.area().addParticle(new ParticleSplash(actor.areaX(), actor.areaY(), UColor.COLOR_WHITE, 75, 0.9f));
        if (commander.random.nextFloat() < 0.5f) {
            cell.area().addParticle(new ParticleSplash(actor.areaX() - 1 + commander.random.nextInt(3),
                    actor.areaY() - 1 + commander.random.nextInt(3),
                    UColor.COLOR_CYAN, 60, 0.6f));
        }
    }
}
