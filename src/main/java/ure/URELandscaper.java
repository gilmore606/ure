package ure;

import ure.terrain.URETerrain;

import java.util.Random;

public class URELandscaper {

    Random rand;

    class Grid {
        boolean cells[][];
        int width;
        int height;
        public Grid(int w, int h) {
            cells = new boolean[w][h];
            width = w;
            height = h;
        }
        public void set(int x, int y, boolean val) {
            if (x>=0 && x<width && y>=0 && y<height)
                cells[x][y] = val;
        }
        public boolean get(int x, int y) {
            if (x>=0 && x<width && y>=0 && y<height)
                return cells[x][y];
            return true;
        }
        public void copyFrom(Grid src) {
            for (int x=0;x<width;x++) {
                for (int y=0;y<height;y++) {
                    cells[x][y] = src.get(x,y);
                }
            }
        }
        public int neighborsAt(int x, int y) { return neighborsAt(x, y, 1); }
        public int neighborsAt(int x, int y, int dist) {
            int neighbors = 0;
            for (int ox=-dist;ox<=dist;ox++) {
                for (int oy=-dist;oy<=dist;oy++) {
                    if (get(x+ox,y+oy))
                        neighbors++;
                }
            }
            return neighbors;
        }
        public int flood(int x, int y) {
            int total = 0;
            if (x<0 || x>=width || y<0 || y>= height)
                return 0;
            if (cells[x][y])
                return 0;
            cells[x][y] = true;
            total++;
            total += flood(x+1,y);
            total += flood(x-1,y);
            total += flood(x,y+1);
            total += flood(x,y-1);
            return total;
        }
    }

    public URELandscaper() {
        rand = new Random();
    }

    public void fillRect(UREArea area, String t, int x1, int y1, int x2, int y2) { drawRect(area, t, x1, y1, x2, y2, true); }
    public void drawRect(UREArea area, String t, int x1, int y1, int x2, int y2) { drawRect(area, t, x1, y1, x2, y2, false); }
    public void drawRect(UREArea area, String t, int x1, int y1, int x2, int y2, boolean filled) {
        for (int x=x1;x<=x2;x++) {
            for (int y=y1;y<=y2;y++) {
                if (filled || y == y1 || y == y2 || x == x1 | x == x2) {
                    area.setTerrain(x, y, t);
                }
            }
        }
    }

    public void digRiver(UREArea area, String t, int x1, int y1, int x2, int y2, float riverWidth, float twist, float twistmax) {
        int width = x2-x1; int height = y2-y1;
        int edge = rand.nextInt(4);
        float startx, starty, dx, dy, ctwist;
        if (edge == 0) {
            starty = (float)y1;  startx = (float)(rand.nextInt(width) + x1);
            dx = 0f ; dy = 1f;
        } else if (edge == 1) {
            starty = (float)height; startx = (float)(rand.nextInt(width) + x1);
            dx = 0f; dy = -1f;
        } else if (edge == 2) {
            startx = (float)x1; starty = (float)(rand.nextInt(height) + y1);
            dx = 1f; dy = 0f;
        } else {
            startx = (float)width; starty = (float)(rand.nextInt(height) + y1);
            dx = -1f; dy = 0f;
        }
        ctwist = 0f;
        boolean hitedge = false;
        while (!hitedge) {
            fillRect(area, t, (int)startx, (int)starty, (int)(startx+riverWidth), (int)(starty+riverWidth));
            startx += dx;
            starty += dy;
            if (rand.nextFloat() < twist) {
                if (dx == 0) {
                    startx += ctwist;
                }
                if (dy == 0) {
                    starty += ctwist;
                }
            }
            if (startx > x2 || startx < x1 || starty > y2 || starty < y1) {
                hitedge = true;
            }
            ctwist = ctwist + rand.nextFloat() * twist - (twist/2f);
            if (ctwist > twistmax) ctwist = twistmax;
            if (ctwist < -twistmax) ctwist = -twistmax;
        }
    }

    public void digCaves(UREArea area, String t, int x1, int y1, int x2, int y2) {
        digCaves(area,t,x1,y1,x2,y2,0.42f,6,4,3);
    }
    public void digCaves(UREArea area, String t, int x1, int y1, int x2, int y2, float initialDensity, int jumblePasses, int jumbleDensity, int smoothPasses) {
        int width = x2-x1; int height = y2-y1;
        Grid map = new Grid(width,height);
        Grid scratchmap = new Grid(width,height);
        float fillratio = 0f;
        while (fillratio < 0.4f) {
            int gapY = rand.nextInt(height / 2) + height / 3;
            for (int x = 0;x < width;x++) {
                for (int y = 0;y < height;y++) {
                    if ((y < gapY || y > gapY + 1) && rand.nextFloat() < initialDensity)
                        map.set(x, y, true);
                    else
                        map.set(x, y, false);
                }
            }
            for (int i = 0;i < jumblePasses;i++) {
                for (int x = 0;x < width;x++) {
                    for (int y = 0;y < height;y++) {
                        if (map.neighborsAt(x, y) >= 5 || map.neighborsAt(x, y, 2) <= jumbleDensity) {
                            scratchmap.set(x, y, true);
                        } else {
                            scratchmap.set(x, y, false);
                        }
                    }
                }
                map.copyFrom(scratchmap);
            }
            for (int i = 0;i < smoothPasses;i++) {
                for (int x = 0;x < width;x++) {
                    for (int y = 0;y < height;y++) {
                        if (map.neighborsAt(x, y) >= 5) {
                            scratchmap.set(x, y, true);
                        } else {
                            scratchmap.set(x, y, false);
                        }
                    }
                }
                map.copyFrom(scratchmap);
            }
            scratchmap.copyFrom(map);
            int x = width / 2;
            int y = height / 2;
            while (scratchmap.get(x, y)) {
                x = rand.nextInt(width - 2) + 2;
                y = rand.nextInt(height - 2) + 2;
            }
            int spacecount = scratchmap.flood(x, y);
            fillratio = (float) spacecount / (float) (width * height);
        }
        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                if (!map.get(x,y) && scratchmap.get(x,y))
                    area.setTerrain(x+x1, y+y1, t);
            }
        }
    }
}
