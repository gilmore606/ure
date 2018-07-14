package ure.terrain;

import java.util.Random;

/**
 * Lava does damage to things that move into it.
 */
public class Lava extends TerrainI implements UTerrain {

    public static final String TYPE = "lava";

    int bubbleFrames = 0;

    @Override
    public char getGlyph() {
        if (bubbleFrames > 0) {
            bubbleFrames--;
            return '.';
        }
        Random r = new Random();
        if (r.nextFloat() > 0.9992)
            bubbleFrames = r.nextInt(60) + 8;
        return super.getGlyph();
    }

    @Override
    public char glyph(int x, int y) {
        return getGlyph();
    }
}
