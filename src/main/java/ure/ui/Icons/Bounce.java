package ure.ui.Icons;

import ure.actors.UActor;

public class Bounce extends Icon  {

    public static final String TYPE = "bounce";

    public Bounce() { super(TYPE); }

    @Override
    int glyphX() {
        if (entity instanceof UActor)
            return -(int)(Bounce(commander.frameCounter) * animAmpX);
        return 0;
    }
    @Override
    int glyphY() {
        if (entity instanceof UActor)
            return -(int)(Bounce(commander.frameCounter) * animAmpY);
        return 0;
    }

    float Bounce(int frame) {
        return (float)(Math.abs(Math.sin((frame + animOffset) * animFreq * 0.4f)) * 8f);
    }
}
