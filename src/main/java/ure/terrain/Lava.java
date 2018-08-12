package ure.terrain;

/**
 * Lava does damage to things that move into it.
 */
public class Lava extends UTerrain {

    public static final String TYPE = "lava";

    protected int bubbleFrames = 0;

    @Override
    public char getGlyph() {
        if (bubbleFrames > 0) {
            bubbleFrames--;
            return '.';
        }
        if (random.nextFloat() > 0.9992)
            bubbleFrames = random.nextInt(60) + 8;
        return super.getGlyph();
    }

    @Override
    public char glyph(int x, int y) {
        return getGlyph();
    }

    public int getBubbleFrames() {
        return bubbleFrames;
    }

    public void setBubbleFrames(int bubbleFrames) {
        this.bubbleFrames = bubbleFrames;
    }
}
