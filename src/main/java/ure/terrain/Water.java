package ure.terrain;

import ure.actors.UActor;
import ure.areas.UCell;
import ure.math.UColor;
import ure.ui.particles.ParticleSplash;

/**
 * Water has a pretty animation.  It should probably do something more interesting.
 *
 */
public class Water extends UTerrain {

    public static final String TYPE = "water";

    public int shimmerFrames = 80;
    public int shimmerVariance = 50;


    public void animationTick() {
        //super.animationTick();
        //if (animationFrame % (shimmerFrames + random.nextInt(shimmerVariance)) == 0) {
        //    applyColorVariance();
       // }
    }

    @Override
    public void walkedOnBy(UActor actor, UCell cell) {
        super.walkedOnBy(actor,cell);
        cell.area().addParticle(new ParticleSplash(actor.areaX(), actor.areaY(), UColor.WHITE, 75, 0.9f));
        if (random.nextFloat() < 0.5f) {
            cell.area().addParticle(new ParticleSplash(actor.areaX() - 1 + random.nextInt(3),
                    actor.areaY() - 1 + random.nextInt(3),
                    UColor.CYAN, 60, 0.6f));
        }
    }
}
