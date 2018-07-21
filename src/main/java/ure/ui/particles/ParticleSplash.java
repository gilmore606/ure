package ure.ui.particles;

import ure.math.UColor;

public class ParticleSplash extends UParticle {

    public ParticleSplash(int _x, int _y, UColor fgColor, int frames, float alpha) {
        super(_x, _y, frames, fgColor, alpha, true);
    }

    @Override
    public char glyph() {
        if (ticksLeft < (ticksInitial * 0.4f)) {
            return '.';
        } else if (ticksLeft < (ticksInitial * 0.7f)) {
            return '*';
        } else {
            return '%';
        }
    }
}
