package ure.areas.gen;

import ure.areas.UArea;

public class Room {
    public int x,y,width,height;
    public Room(int x, int y, int width, int height) {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }
    public Room(int width, int height) {
        this.width=width;
        this.height=height;
        this.x=-1;
        this.y=-1;
    }
    public Face[] faces() {
        Face[] faces = new Face[4];
        faces[0] = new Face(x,y-1,width,0,-1);
        faces[1] = new Face(x+width,y,height,1,0);
        faces[2] = new Face(x,y+height,width,0,1);
        faces[3] = new Face(x-1,y,height,-1,0);
        return faces;
    }
    public void rotate() {
        int tmp = width;
        width = height;
        height = tmp;
    }
    public void print(Shape space) { print(space, false); }
    public void print(Shape space, boolean rounded) {
        for (int xi=0;xi<width;xi++) {
            for (int yi = 0;yi < height;yi++) {
                if (!rounded || !(xi==0 || xi==width-1) || !(yi==0 || yi==height-1))
                    space.set(x + xi, y + yi);
            }
        }
    }
    public void punchDoors(Shape space) { punchDoors(space, false); }
    public void punchDoors(Shape space, boolean punchAll) {
        for (Face face : faces()) {
            face.punchDoors(space, punchAll);
        }
    }
    public boolean isHallway() {
        if ((width*3<height) || (height*3<width))
            return true;
        return false;
    }
    public boolean unobstructed(UArea area) {
        if (area.terrainAt(x+1,y+1) != null)
            return area.terrainAt(x+1,y+1).passable();
        return false;
    }
    public boolean unobstructed(Shape shape) { return shape.value(x+1,y+1); }

    public boolean touches(Room r) {
        if (x > r.x+r.width || y > r.y+r.height || x+width < r.x || y+height < r.y)
            return false;
        return true;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
