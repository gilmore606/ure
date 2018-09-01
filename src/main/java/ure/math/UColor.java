package ure.math;

/**
 * UColor implements a mutable RGBA color with color and light mixing.
 * Consider the mutability carefully when passing UColor variables; a UColor you return as a state
 * from an entity could be modified.
 */
public class UColor {
    public float r, g, b, a;

    public static UColor CLEAR = new UColor(0f,0f,0f,0f);
    public static UColor BLACK = new UColor(0f,0f,0f);
    public static UColor SHADE = new UColor(0f,0f,0f,0.1f);
    public static UColor DARKSHADE = new UColor(0f,0f,0f,0.22f);
    public static UColor DARKERSHADE = new UColor(0f,0f,0f,0.4f);
    public static UColor LIGHT = new UColor(1f,1f,1f,0.3f);
    public static UColor WHITE = new UColor(1f,1f,1f);
    public static UColor OFFWHITE = new UColor(1f,1f,0.9f);
    public static UColor GRAY = new UColor(0.5f, 0.5f, 0.5f);
    public static UColor DARKGRAY = new UColor(0.25f, 0.25f, 0.25f);
    public static UColor LIGHTGRAY = new UColor(0.75f, 0.75f, 0.75f);
    public static UColor RED = new UColor(1f, 0f, 0f);
    public static UColor LIGHTRED = new UColor(1f, 0.4f, 0.4f);
    public static UColor GREEN = new UColor(0f, 1f, 0f);
    public static UColor BLUE = new UColor(0f,0f,1f);
    public static UColor LIGHTBLUE = new UColor(0.6f, 0.6f, 1f);
    public static UColor YELLOW = new UColor(1f,1f,0f);
    public static UColor MAGENTA = new UColor(1f,0f,1f);
    public static UColor CYAN = new UColor(0f,1f,1f);

    public UColor() {}
    public UColor(int ir, int ig, int ib) {
        set(ir,ig,ib);
    }
    public UColor(float fr, float fg, float fb) {
        set(fr,fg,fb);
    }
    public UColor(float fr, float fg, float fb, float fa) { set(fr,fg,fb,fa); }
    public UColor(UColor color) { set(color.fR(),color.fG(),color.fB()); }

    /**
     * FCC luminance weights
     */
    private float lumR = 0.299f;
    private float lumG = 0.587f;
    private float lumB = 0.114f;

    void BoundsCheck() {
        if (r > 1f) r = 1f;
        if (g > 1f) g = 1f;
        if (b > 1f) b = 1f;
        if (a > 1f) a = 1f;
        if (r < 0f) r = 0f;
        if (g < 0f) g = 0f;
        if (b < 0f) b = 0f;
        if (a < 0f) a = 0f;
    }

    /**
     *
     * @return the integer 0-255 red value.
     */
    public int iR() {
        return (int)(r * 255f);
    }

    /**
     *
     * @return the integer 0-255 green value.
     */
    public int iG() {
        return (int)(g * 255f);
    }

    /**
     *
     * @return the integer 0-255 blue value.
     */
    public int iB() {
        return (int)(b * 255f);
    }

    /**
     *
     * @return the integer 0-255 alpha value.
     */
    public int iA() { return (int)(a * 255f); }

    /**
     *
     * @return the float 0f-1f red value.
     */
    public float fR() {
        return r;
    }

    /**
     *
     * @return the float 0f-1f green value.
     */
    public float fG() {
        return g;
    }

    /**
     *
     * @return the float 0f-1f blue value.
     */
    public float fB() {
        return b;
    }

    /**
     *
     * @return the float 0f-1f alpha value.
     */
    public float fA() { return a; }

    /**
     * Set the color with integer 0-255 values.
     */
    public void set (UColor source) {
        set(source.fR(),source.fG(),source.fB(),source.fA());
    }
    public void set (int ir, int ig, int ib) { set(ir,ig,ib,255); }
    public void set(int ir, int ig, int ib, int ia) {
        r = (float)ir / 255f;
        g = (float)ig / 255f;
        b = (float)ib / 255f;
        a = (float)ia / 255f;
        BoundsCheck();
    }

    public void setR(float f) { r = f; BoundsCheck(); }
    public void setG(float f) { g = f; BoundsCheck(); }
    public void setB(float f) { b = f; BoundsCheck(); }

    /**
     * Set the color with float 0f-1f values.
     * @param fr
     * @param fg
     * @param fb
     */
    public void set(float fr, float fg, float fb) { set (fr,fg,fb,1f); }
    public void set(float fr, float fg, float fb, float fa) {
        r = fr;
        g = fg;
        b = fb;
        a = fa;
        BoundsCheck();
    }

    /**
     * Set the color with an array of 3 ints (R,G,B).
     * @param arr
     */
    public void set(int[] arr) {
        set(arr[0],arr[1],arr[2]);
    }

    public void setAlpha(float alpha) {
        a = alpha;
    }
    /**
     * Get the grayscale value of this color according to FCC luminance.
     *
     * @return
     */
    public float grayscale() {
        return r * lumR + g * lumG + b * lumB;
    }

    /**
     * Brighten/darken this color by the float intensity.
     * @param intensity Above 1.0 brightens, below 1.0 darkens.
     */
    public void brightenBy(float intensity) {
        r = r * intensity;
        g = g * intensity;
        b = b * intensity;
        BoundsCheck();
    }

    /**
     * Desaturate a color toward zero saturation (1f).
     *
     * @param amount
     */
    public void desaturateBy(float amount) {
        if (amount == 0f) return;
        float gray = grayscale();
        r = gray * amount + r * (1f - amount);
        g = gray * amount + g * (1f - amount);
        b = gray * amount + b * (1f - amount);
        BoundsCheck();
    }

    /**
     * Add a colored light to this light.
     * TODO: Make this a more realistic mix.  Implement bloom.
     * @param light The light color to add.
     * @param intensity The amount of light to add to this light.
     */
    public void addLights(UColor light, float intensity) {
        addLights(light.fR(), light.fG(), light.fB(), intensity);
    }
    public void addLights(float fr, float fg, float fb, float intensity) {
        r = r + fr * intensity;
        g = g + fg * intensity;
        b = b + fb * intensity;
        BoundsCheck();
    }

    /**
     * Illuminate this entity color with a summed colored light.
     * TODO: Make this a more interesting algorithm.
     * @param light The sum of colored light illuminating this color.
     * @param brightness The amount of light to add.
     */
    public void illuminateWith(UColor light, float brightness) {
        float lr = light.fR();
        float lg = light.fG();
        float lb = light.fB();
        float or = lr * r * brightness;
        float og = lg * g * brightness;
        float ob = lb * b * brightness;
        set(or,og,ob);
    }

}
