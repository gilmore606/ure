package ure.ui.particles;

import ure.math.UColor;

/**
 * A hit on an actor.  Spray out blood particles too.
 */

public class ParticleHit extends UParticle {

    float intensity;
    UColor bloodColor;

    public ParticleHit(int _x, int _y, UColor bloodColor, float intensity) {
        super(_x, _y, 8, UColor.COLOR_YELLOW, intensity, false);
        this.intensity = intensity;
        this.bloodColor = bloodColor;
        glyphFrames = "X*X*X*+*+.   ";
    }

    @Override
    public void animationTick() {
        super.animationTick();
        if (ticksInitial-ticksLeft == 1) {
            area.addParticle(new ParticleBlood(x,y,-1,0,bloodColor,intensity));
            area.addParticle(new ParticleBlood(x,y,-1,-1,bloodColor,intensity));
            area.addParticle(new ParticleBlood(x,y,0,-1,bloodColor,intensity));
            area.addParticle(new ParticleBlood(x,y,1,-1,bloodColor,intensity));
            area.addParticle(new ParticleBlood(x,y,1,0,bloodColor,intensity));
            area.addParticle(new ParticleBlood(x,y,1,1,bloodColor,intensity));
            area.addParticle(new ParticleBlood(x,y,0,1,bloodColor,intensity));
            area.addParticle(new ParticleBlood(x,y,-1,1,bloodColor,intensity));
        }
    }

    @Override
    public int glyphOffsetX() {
        return glyphOffsetY();
    }
    public int glyphOffsetY() {
        return commander.random.nextInt(5)-3;
    }
}
