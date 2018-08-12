package ure.ui.particles;

import ure.math.UColor;

public class ParticleStain extends UParticle {

    int fullFrames;

    public ParticleStain(int x, int y, UColor fgColor, int glyph, int fullFrames, int fadeFrames, float alpha) {
        super(x,y,fullFrames+fadeFrames,fgColor,alpha,true,0,0,0,0,0);
        this.fullFrames = fullFrames;
        this.alphadecay = alpha / fadeFrames;
        this.glyph = glyph;
    }

    @Override
    public void animationTick() {
        ticksLeft--;
        if (frame() > fullFrames) {
            alpha -= alphadecay;
        }
    }
}
