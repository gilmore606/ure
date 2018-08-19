package ure.ui.particles;

import ure.math.UColor;

public class ParticleTalk extends UParticle {

    public ParticleTalk(int _x, int _y) {
        super(_x, _y, 20, UColor.WHITE, 0.7f, false,0,-0.8f,0.03f, 0,0);
        glyphFrames = "oooOOOoooOOOoooOOOoooOOOooo................";
        offy = 3-config.getTileHeight();
        vecx = 0.4f-random.f(0.8f);
    }

}
