package ure.areas.gen;

import java.util.ArrayList;
import java.util.List;

public class Face {
    public int x, y, length;
    int facex, facey;

    public Face(int x, int y, int length, int facex, int facey) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.facex = facex;
        this.facey = facey;
    }

    /**
     * Try to add the room somewhere along me.
     * If we can, record the room's xy and return it, else return null.
     */
    public Room addRoom(Room room, Shape space) {
        ArrayList<Integer> spaces = new ArrayList<>();
        for (int i = -(room.width - 3);i < length - 3;i++) {
            boolean blocked = false;
            for (int cx = -1;cx < room.width + 1;cx++) {
                for (int cy = 0;cy < room.height + 2;cy++) {
                    int tx = transX(cx + i, cy);
                    int ty = transY(cx + i, cy);
                    if (space.value(tx, ty) || !space.isValidXY(tx, ty)) {
                        blocked = true;
                        break;
                    }
                }
                if (blocked) break;
            }
            if (!blocked) spaces.add(i);
        }
        if (spaces.size() == 0) return null;
        int i = (int) space.random.member((List) spaces);
        room.x = transX(i, 1);
        room.y = transY(i, 1);
        if (facey == -1) {
            room.y -= room.height - 1;
        } else if (facex == -1) {
            room.rotate();
            room.x -= room.width - 1;
        } else if (facex == 1) {
            room.rotate();
        }
        return room;
    }

    /**
     * Translate coordinates from my relative space to absolute space.
     */
    public int transX(int dx, int dy) {
        if (facey == -1 || facey == 1) {
            return x + dx;
        } else if (facex == 1) {
            return x + dy;
        } else {
            return x - dy;
        }
    }

    public int transY(int dx, int dy) {
        if (facey == -1) {
            return y - dy;
        } else if (facey == 1) {
            return y + dy;
        } else {
            return y + dx;
        }
    }

    /**
     * Punch a doorhole somewhere along us, if possible.
     */
    public void punchDoors(Shape space) {
        punchDoors(space, false);
    }

    public void punchDoors(Shape space, boolean punchAll) {
        for (int i : space.random.seq(length)) {
            int fx = x + (i * Math.abs(facey));
            int fy = y + (i * Math.abs(facex));
            if (space.value(fx + facex, fy + facey) && space.value(fx - facex, fy - facey)) {
                space.set(fx, fy);
                if (!punchAll) return;
            }
        }
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFacex() {
        return facex;
    }

    public void setFacex(int facex) {
        this.facex = facex;
    }

    public int getFacey() {
        return facey;
    }

    public void setFacey(int facey) {
        this.facey = facey;
    }
}
