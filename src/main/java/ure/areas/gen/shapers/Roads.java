package ure.areas.gen.shapers;

import ure.areas.gen.Shape;

public class Roads extends Shaper {

    public Roads(int xsize, int ysize) {
        super(xsize,ysize);
        name = "Roads";
    }

    @Override
    public void setupParams() {
        addParamI("roadsMin", 1, 1, 20);
        addParamI("roadsMax",1,3,20);
        addParamF("width", 1f, 3f, 8f);
        addParamF("twist", 0f, 0.5f, 2f);
        addParamF("twistMax", 0f, 1f, 4f);
    }

    @Override
    public void build() {
        buildRoads(getParamI("roadsMin"),getParamI("roadsMax"),getParamF("width"),getParamF("twist"),getParamF("twistMax"));
    }

    public void buildRoads(int roadsMin, int roadsMax, float width, float twist, float twistmax) {
        clear();
        int numRoads = random.i(roadsMin,roadsMax);
        for (int i=0;i<numRoads;i++) {
            int edge = random.i(4);
            float startx, starty, dx, dy, ctwist;
            if (edge == 0) {
                starty = 0f;
                startx = (float) (random.i(xsize));
                dx = 0f;
                dy = 1f;
            } else if (edge == 1) {
                starty = (float) ysize;
                startx = (float) (random.i(xsize));
                dx = 0f;
                dy = -1f;
            } else if (edge == 2) {
                startx = 0f;
                starty = (float) (random.i(ysize));
                dx = 1f;
                dy = 0f;
            } else {
                startx = (float) xsize;
                starty = (float) (random.i(ysize));
                dx = -1f;
                dy = 0f;
            }
            ctwist = 0f;
            boolean hitedge = false;
            while (!hitedge) {
                fillRect((int) startx, (int) starty, (int) (startx + width), (int) (starty + width));
                startx += dx;
                starty += dy;
                if (random.f() < twist) {
                    if (dx == 0) {
                        startx += ctwist;
                    }
                    if (dy == 0) {
                        starty += ctwist;
                    }
                }
                if (startx >= xsize || startx < 0 || starty >= ysize || starty < 0) {
                    hitedge = true;
                }
                ctwist = ctwist + random.f(twist) - (twist / 2f);
                if (ctwist > twistmax) ctwist = twistmax;
                if (ctwist < -twistmax) ctwist = -twistmax;
            }
        }
    }
}
