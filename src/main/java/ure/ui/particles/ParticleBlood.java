package ure.ui.particles;

import ure.math.UColor;

public class ParticleBlood  extends UParticle {

    final static int[] glyphs = new int[]{39,44,46};

    public ParticleBlood(int _x, int _y, UColor fgColor, float intensity, float vecx, float vecy) {
        super(_x,_y,12, fgColor,intensity,true, vecx, vecy,0.42f, 0, 0);
        glyph = (char)random.member(glyphs);
    }
}
