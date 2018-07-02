package ure;

import java.awt.*;

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
    public UColor(Color color) {
        set(color.getRed(),color.getGreen(),color.getBlue());
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

    public int iR() {
        return (int)(r * 255f);
    }
    public int iG() {
        return (int)(g * 255f);
    }
    public int iB() {
        return (int)(b * 255f);
    }
    public int iA() { return (int)(a * 255f); }
    public float fR() {
        return r;
    }
    public float fG() {
        return g;
    }
    public float fB() {
        return b;
    }
    public float fA() { return a; }

    public void set (int ir, int ig, int ib) { set(ir,ig,ib,255); }
    public void set(int ir, int ig, int ib, int ia) {
        r = (float)ir / 255f;
        g = (float)ig / 255f;
        b = (float)ib / 255f;
        a = (float)ia / 255f;
        BoundsCheck();
    }
    public void set(float fr, float fg, float fb) { set (fr,fg,fb,1f); }
    public void set(float fr, float fg, float fb, float fa) {
        r = fr;
        g = fg;
        b = fb;
        a = fa;
        BoundsCheck();
    }
    public void set(int[] arr) {
        set(arr[0],arr[1],arr[2]);
    }
    public void brightenBy(float intensity) {
        r = r * intensity;
        g = g * intensity;
        b = b * intensity;
        BoundsCheck();
    }

    public void addLights(Color light, float intensity) {
        addLights((float)light.getRed() / 255f, (float)light.getGreen() / 255f, (float)light.getBlue() / 255f, intensity);
    }
    public void addLights(UColor light, float intensity) {
        addLights(light.fR(), light.fG(), light.fB(), intensity);
    }
    public void addLights(float fr, float fg, float fb, float intensity) {
        r = r + fr * intensity;
        g = g + fg * intensity;
        b = b + fb * intensity;
        BoundsCheck();
    }

    public void illuminateWith(UColor light, float brightness) {
        float lr = light.fR();
        float lg = light.fG();
        float lb = light.fB();
        float or = lr * r * brightness;
        float og = lg * g * brightness;
        float ob = lb * b * brightness;
        set(or,og,ob);
    }

    public Color makeAWTColor() {
        return new Color((int)(r * 255f), (int)(g * 255f), (int)(b * 255f));
    }
}
