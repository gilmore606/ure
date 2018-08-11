package ure.ui.Icons;

public class Bounce extends Icon  {

    public static final String TYPE = "bounce";

    public Bounce() { super(TYPE); }

    @Override
    public int glyphX() {
        return -(int)(Bounce(commander.frameCounter) * animAmpX);
    }
    @Override
    public int glyphY() {
        return -(int)(Bounce(commander.frameCounter) * animAmpY);
    }

    float Bounce(int frame) {
        return (float)(Math.abs(Math.sin(frame * animFreq * 0.3f)) * 8f);
    }
}
