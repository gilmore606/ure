package ure.terrain;

import java.util.Random;

public class Lava extends URETerrain {

    public static final String TYPE = "lava";

    int bubbleFrames = 0;

    @Override
    public char glyph() {
        if (bubbleFrames > 0) {
            bubbleFrames--;
            return '.';
        }
        Random r = new Random();
        if (r.nextFloat() > 0.9992)
            bubbleFrames = r.nextInt(60) + 8;
        return super.glyph();
    }

    @Override
    public char glyph(int x, int y) {
        return glyph();
    }
}
