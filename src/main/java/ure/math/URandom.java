package ure.math;

import java.util.List;
import java.util.Random;

public class URandom extends Random {

    public int i(int bound) {
        return nextInt(bound);
    }

    public int i(int min, int max) { return min + nextInt(max-min+1); }

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

    /**
     * Random sequence from 0 to len.
     */
    public int[] seq(int len) {
        int[] seq = new int[len];
        for (int i=0;i<len;i++)
            seq[i] = i;
        for (int i=len-1;i>0;i--) {
            int j = i(i);
            int tmp = seq[j];
            seq[j] = seq[i];
            seq[i] = tmp;
        }
        return seq;
    }

}
