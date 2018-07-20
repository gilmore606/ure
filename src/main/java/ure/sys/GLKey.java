package ure.sys;

/**
 * I'm disgusted.
 */
public class GLKey {

    public int k;
    public boolean shift;
    public boolean ctrl;

    public GLKey(int _k, boolean _shift, boolean _ctrl) {
        k = _k;
        shift = _shift;
        ctrl = _ctrl;
    }
    public GLKey(int _k) {
        k = _k;
        shift = false;
        ctrl = false;
    }
    public boolean sameKeyAs(GLKey k2) {
        if (k2.k == k && k2.shift == shift && k2.ctrl == ctrl)
            return true;
        return false;
    }
}
