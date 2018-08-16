package ure.math;

import java.util.List;
import java.util.Random;

public class URandom extends Random {

    public int i(int bound) {
        return nextInt(bound);
    }

    public float f() { return f(1f); }

    public float f(float bound) {
        return nextFloat()*bound;
    }

    public Object member(List<Object> list) {
        return list.get(i(list.size()));
    }

    public Object member(Object[] list) { return list[i(list.length)]; }

    public int member(int[] list) { return list[i(list.length)]; }

    public float member(float[] list) { return list[i(list.length)]; }

    public String member(String[] list) { return list[i(list.length)]; }

}
