package ure.sys;

import static org.lwjgl.glfw.GLFW.*;

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

    public String typed() {
        String s = rawtyped();
        if (s == null) return null;
        if (shift) {
            if (s.equals("-")) return "_";
            if (s.equals("=")) return "+";
            if (s.equals("1")) return "!";
            if (s.equals("2")) return "@";
            if (s.equals("3")) return "#";
            if (s.equals("4")) return "$";
            if (s.equals("5")) return "%";
            if (s.equals("6")) return "^";
            if (s.equals("7")) return "&";
            if (s.equals("8")) return "*";
            if (s.equals("9")) return "(";
            if (s.equals("0")) return ")";
            if (s.equals("`")) return "~";
            s = s.toUpperCase();
        }
        return s;
    }
    public String rawtyped() {
        switch (k) {
            case GLFW_KEY_A: return "a";
            case GLFW_KEY_B: return "b";
            case GLFW_KEY_C: return "c";
            case GLFW_KEY_D: return "d";
            case GLFW_KEY_E: return "e";
            case GLFW_KEY_F: return "f";
            case GLFW_KEY_G: return "g";
            case GLFW_KEY_H: return "h";
            case GLFW_KEY_I: return "i";
            case GLFW_KEY_J: return "j";
            case GLFW_KEY_K: return "k";
            case GLFW_KEY_L: return "l";
            case GLFW_KEY_M: return "m";
            case GLFW_KEY_N: return "n";
            case GLFW_KEY_O: return "o";
            case GLFW_KEY_P: return "p";
            case GLFW_KEY_Q: return "q";
            case GLFW_KEY_R: return "r";
            case GLFW_KEY_S: return "s";
            case GLFW_KEY_T: return "t";
            case GLFW_KEY_U: return "u";
            case GLFW_KEY_V: return "v";
            case GLFW_KEY_W: return "w";
            case GLFW_KEY_X: return "x";
            case GLFW_KEY_Y: return "y";
            case GLFW_KEY_Z: return "z";
            case GLFW_KEY_0: return "0";
            case GLFW_KEY_1: return "1";
            case GLFW_KEY_2: return "2";
            case GLFW_KEY_3: return "3";
            case GLFW_KEY_4: return "4";
            case GLFW_KEY_5: return "5";
            case GLFW_KEY_6: return "6";
            case GLFW_KEY_7: return "7";
            case GLFW_KEY_8: return "8";
            case GLFW_KEY_9: return "9";
            case GLFW_KEY_SPACE: return " ";
            case GLFW_KEY_MINUS: return "-";
            case GLFW_KEY_EQUAL: return "=";
            case GLFW_KEY_SEMICOLON: return ";";
            case GLFW_KEY_APOSTROPHE: return "'";
        }
        return null;
    }
}
