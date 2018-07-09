package ure.math;

/**
 * UColor implements a mutable RGBA color with color and light mixing.
 * Consider the mutability carefully when passing UColor variables; a UColor you return as a state
 * from an entity could be modified.
 */
public class UColor {
    public float r, g, b, a;

    public static UColor COLOR_BLACK = new UColor(0f,0f,0f);
    public static UColor COLOR_WHITE = new UColor(1f,1f,1f);

    public UColor(int ir, int ig, int ib) {
        set(ir,ig,ib);
    }
    public UColor(float fr, float fg, float fb) {
        set(fr,fg,fb);
    }
    public UColor(UColor color) { set(color.fR(),color.fG(),color.fB()); }

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
    public void set (int ir, int ig, int ib) { set(ir,ig,ib,255); }
    public void set(int ir, int ig, int ib, int ia) {
        r = (float)ir / 255f;
        g = (float)ig / 255f;
        b = (float)ib / 255f;
        a = (float)ia / 255f;
        BoundsCheck();
    }

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
     * Add a colored light to this light.
     * TODO: Make this a more realistic mix.
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
