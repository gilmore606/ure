package ure.ui.particles;

import ure.math.UColor;

public class ParticleBlood  extends UParticle {

    public ParticleBlood(int _x, int _y, UColor fgColor, float intensity, float vecx, float vecy) {
        super(_x,_y,12, fgColor,intensity,true, vecx, vecy,-vecx*0.1f,-vecy*0.1f);
    }
}
