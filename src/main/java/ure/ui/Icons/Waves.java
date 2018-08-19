package ure.ui.Icons;

import ure.math.UColor;

public class Waves extends Icon {

    public static final String TYPE = "waves";

    public Waves() { super(TYPE); }

    @Override
    public int glyphY() {
        return (int)(Wave(commander.frameCounter + animOffset) * animAmpY);
    }

    float Wave(int frame) {
        if (animFreq == 0f) return 0f;
        int cycle = (int)((1f - animFreq) * 120f);
        int mid = cycle / 2;
        int f = frame % cycle;
        if (f > mid + 1)
            f = cycle - f;
        float n = (float)f / (float)(cycle / 2);
        return (float)Math.sin((double)n * 6.28) * 5f;
    }

}
