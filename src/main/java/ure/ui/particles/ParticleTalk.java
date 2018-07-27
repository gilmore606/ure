package ure.ui.particles;

import ure.math.UColor;

public class ParticleTalk extends UParticle {

    public ParticleTalk(int _x, int _y) {
        super(_x, _y, 20, UColor.COLOR_WHITE, 1f, false);
        glyphFrames = "oooOOOoooOOOoooOOOoooOOOooo................";
    }

    @Override
    public int glyphOffsetY() {
        return -8 - (ticksInitial-ticksLeft);
    }
}
