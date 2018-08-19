package ure.ui.particles;

import ure.math.UColor;

public class ParticleSpark extends UParticle {

    public ParticleSpark(int _x, int _y, UColor fgColor, int frames, float alpha) {
        super(_x,_y,frames,fgColor,alpha,false,0,0,-0.1f, 0,0);
        vecy = -0.2f-random.f(0.2f);
        gravx = 0.2f-random.f(0.4f);
        offx = random.i(config.getTileWidth());
        offy = -config.getTileHeight()/2 - random.i(config.getTileHeight()/2);
    }

    @Override
    public int glyph() {
        if (ticksLeft < ticksInitial * 0.4f) {
            return '.';
        } else if (ticksLeft < ticksInitial * 0.6f) {
            return '`';
        } else if (ticksLeft < ticksInitial * 0.8f) {
            return '\'';
        } else {
            return ',';
        }
    }

}
