package ure.ui.particles;

import ure.math.UColor;

public class ParticleTalk extends UParticle {

    public ParticleTalk(int _x, int _y) {
        super(_x, _y, 20, UColor.WHITE, 1f, false,0,0,0, 0,0);
        glyphFrames = "oooOOOoooOOOoooOOOoooOOOooo................";
    }

    @Override
    public int glyphY() {
        return -8 - (ticksInitial-ticksLeft);
    }
}
