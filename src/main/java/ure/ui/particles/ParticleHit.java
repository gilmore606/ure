package ure.ui.particles;

import ure.math.UColor;

/**
 * A hit on an actor.  Spray out blood particles too.
 */

public class ParticleHit extends UParticle {

    float intensity;
    UColor bloodColor;

    public ParticleHit(int _x, int _y, UColor bloodColor, float intensity) {
        super(_x, _y, 8, UColor.YELLOW, intensity, false,0,0,0,0);
        this.intensity = intensity;
        this.bloodColor = bloodColor;
        glyphFrames = "X*X*X*+*+... ";
    }

    @Override
    public void animationTick() {
        super.animationTick();
        if (ticksInitial-ticksLeft == 1) {
            int angle = 0;
            for (int i=0;i<9;i++) {
                angle += random.i(360/5);
                float speed = 4f+random.f(3f);
                float px = speed*(float)Math.cos(Math.toRadians(angle));
                float py = speed*(float)Math.sin(Math.toRadians(angle));
                area.addParticle(new ParticleBlood(x,y,bloodColor,intensity*2f,px,py));
            }
        }
    }

    @Override
    public int glyphX() {
        return glyphY();
    }
    public int glyphY() {
        return random.i(5)-3;
    }
}
