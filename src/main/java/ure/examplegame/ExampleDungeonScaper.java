package ure.examplegame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.areas.UArea;
import ure.areas.gen.ULandscaper;
import ure.math.UColor;
import ure.terrain.UTerrain;

import java.util.ArrayList;
import java.util.Collections;

public class ExampleDungeonScaper extends ULandscaper {

    public static final String TYPE = "dungeonscaper";

    private Log log = LogFactory.getLog(ExampleDungeonScaper.class);

    public ExampleDungeonScaper() {
        super(TYPE);
    }

    class RoomStruct{
        int x, y, w, h, weight;
        int seen, start, exit;
        int entered, hit;
        int size;
        RoomStruct(int x_, int y_, int w_, int h_){
            x = x_; y = y_; w = w_; h = h_;
            weight = random.nextInt(255);

            seen = 0;
            start = 0;
            exit = 0;

            entered = 0; // 0 no, 1 pathfind, 2 branch
            hit = 0;
            size = 0;
        }
        void print(){
            log.debug("ROOM: x: " + x + " y: " + y + " w: " + w + " h: " + h + " weight: " + weight + " size: " + size);
        }
    }

    class RoomNeighbor{
        int x, y;
        RoomNeighbor(int x_, int y_) {
            x = x_;
            y = y_;
        }
    }

    UArea area;
    int cellSize = 5;
    int totalRooms = 0;
    int[][] roomPointers;
    float branchChance = .25f; // Keep in mind this is a chance per roomMultiplier size,
                                // meaning if a room has 4 connection points to another room, it has four chances to make this roll.
    ArrayList<RoomStruct> rooms = new ArrayList<RoomStruct>();
    int ys;
    int xs;
    void makeRoom(int xStart, int yStart){
        if(roomPointers[yStart][xStart] != -1) return;
        int w = Math.max(1, random.nextInt(5));
        int h = Math.max(1, random.nextInt(5));

        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                if(yStart + y >= ys || xStart + x >= xs || roomPointers[yStart + y][xStart + x] != -1){
                    h = y;
                }
            }
        }
        h = Math.max(1, h);
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                if(yStart + y >= ys || xStart + x >= xs || roomPointers[yStart + y][xStart + x] != -1){
                    w = x;
                }
            }
        }
        w = Math.max(1, w);
        RoomStruct room = new RoomStruct(xStart, yStart, w, h);
        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                roomPointers[yStart + y][xStart + x] = totalRooms;
                room.size++;
            }
        }
        room.print();
        totalRooms++;
        rooms.add(room);
    }

    void floodRoom(RoomStruct r, UColor c, boolean walls){
        //TODO: Perhaps add a system to add a bunch of random things to the rooms (furniture/decals/etc).
        if(r.entered == 0) return;
        int x, y;
        for(y = cellSize * r.y;y < cellSize * (r.y + r.h);y++){
            for(x = cellSize * r.x;x < cellSize * (r.x + r.w);x++){
                UTerrain t = area.terrainAt(x, y);
                if (t != null) {
                    if (x == cellSize * r.x || y == cellSize * r.y) {
                        //t.bgColor.set(0, 0, 0);
                        //t.fgColor.set(0, 0, 0);
                        //TODO: If not a door, make it a wall.
                        //  Currently the world starts off as walls, so it's not needed right now,
                        //  but things *CAN* cover existing doors, so we gotta fix that.
                        //TODO: Add right/bottom sides of room walls too when we do this without a wall flood fill.

                        //if(walls) area.setTerrain(x, y, "wall");
                    }else{
                        //t.bgColor.set(c.r, c.g, c.b);
                        //t.fgColor.set(c.r, c.g, c.b);
                        area.setTerrain(x, y, "floor");
                    }
                }
            }
        }
    }

    //TODO: SOMEONE FIX THIS.  MM:A
/*    void colorRoom(RoomStruct r, UColor c){
        if(r.entered == 0) return;
        int x, y;
        for(y = cellSize * r.y; y < cellSize * (r.y + r.h); y++){
            for(x = cellSize * r.x; x < cellSize * (r.x + r.w); x++){
                UTerrain t = area.terrainAt(x, y);
                if (t != null) {
                    if (x == cellSize * r.x || y == cellSize * r.y) {
                    }else{
                        t.bgColor.set(c.r, c.g, c.b);
                        //area.setTerrain(x, y, "floor");
                    }
                }
            }
        }
    }
*/
    void floodRooms(){
        for(RoomStruct r: rooms){
            float f = ((float)(r.weight & 255)) / 255.f;
            floodRoom(r, new UColor(f, f, f), true);
            //MM:A
            //if(r.forceColor != null) colorRoom(r, r.forceColor);
        }
    }

    ArrayList<RoomNeighbor> adjacentRooms(RoomStruct room){
        ArrayList<RoomNeighbor> neighbors = new ArrayList<RoomNeighbor>();
        int lastRoom = -1;
        int r;
        if (room.y + -1 > 0) for (int x = 0; x < room.w; x++) {
            if (room.x + x < 0 || room.x + x >= xs) continue;
            r = roomPointers[room.y - 1][room.x + x];
            if(r != lastRoom && r != -1) neighbors.add(new RoomNeighbor(room.x + x, room.y - 1));
        }
        if (room.y + room.h < ys) for (int x = 0; x < room.w; x++) {
            if (room.x + x < 0 || room.x + x >= xs) continue;
            r = roomPointers[room.y + room.h][room.x + x];
            if(r != lastRoom && r != -1) neighbors.add(new RoomNeighbor(room.x + x, room.y + room.h));
        }
        if (room.x + -1 > 0) for (int y = 0; y < room.h; y++) {
            if (room.y + y < 0 || room.y + y >= ys) continue;
            r = roomPointers[room.y + y][room.x - 1];
            if(r != lastRoom && r != -1) neighbors.add(new RoomNeighbor(room.x - 1, room.y + y));
        }
        if (room.x + room.w < xs) for (int y = 0; y < room.h; y++) {
            if (room.y + y < 0 || (room.y + y) >= ys) continue;
            r = roomPointers[room.y + y][room.x + room.w];
            if(r != lastRoom && r != -1) neighbors.add(new RoomNeighbor(room.x + room.w, room.y + y));
        }
        return neighbors;
    }

    void connect(RoomStruct s, RoomStruct e){
        ArrayList<RoomNeighbor> r = adjacentRooms(s);
        Collections.shuffle(r);
        s.entered = 1;
        e.entered = 1;
        for(RoomNeighbor n : r){
            if(rooms.get(roomPointers[n.y][n.x]) == e){
                if(n.x == s.x + s.w || n.x == s.x - 1){ // E / W door
                    int x = 0;
                    if(n.x == s.x - 1) x = 5;
                    int y = random.nextInt(3) + 2;
                    area.setTerrain(x + n.x * cellSize, y + n.y * cellSize, "door");
                }else{ // N / S
                    int y = 0;
                    if(n.y == s.y - 1) y = 5;
                    int x = random.nextInt(3) + 2;
                    area.setTerrain(x + n.x * cellSize, y + n.y * cellSize, "door");
                }
                return;
            }
        }
    }

    int maxDepth = 256; // Should never get above like 20 or so
    boolean pathFind(RoomStruct room, int depth){
        //floodRoom(room, new UColor(0.0f, 1.0f, 0.0f), false);
        room.hit = 1;
        ArrayList<RoomNeighbor> r = adjacentRooms(room);
        if(depth >= maxDepth) return false;

        boolean ok = true;
        while(ok){
            int max = -1;
            int p;
            RoomStruct next = null;
            for(RoomNeighbor n : r) {
                p = roomPointers[n.y][n.x];
                if (rooms.get(p).hit == 0 && rooms.get(p).weight > max) {
                    max = rooms.get(p).weight;
                    next = rooms.get(p);
                }
            }
            if(next == null){
                return false; // We're exhausted!
            }
            if(next.exit != 0){
                connect(room, next);
                return true; // Ding ding!
            }
            if(pathFind(next, depth + 1)){
                //floodRoom(next, new UColor(1.0f, 1.0f, 0.0f), false);
                connect(room, next);
                return true;
            }
        }
        return false;
    }

    @Override
    public void buildArea(UArea area_, int level, String[] tags) {
        //NOTE:  Ideally you'd want to create this as a divisor of cellSize + 1 so the east/south walls can be walls.
        area = area_;
        fillRect(area, "wall", 0,0,area.xsize - 1,area.ysize - 1);

        ys = (area.ysize - 1) / cellSize;
        xs = (area.xsize - 1) / cellSize;
        roomPointers = new int[ys][xs];

        int x, y;
        for(y = 0; y < ys; y++)
            for(x = 0; x < xs; x++)
                roomPointers[y][x] = -1;


        for(y = 0; y < ys; y++)
            for(x = 0; x < xs; x++)
                makeRoom(x, y);


        RoomStruct start = rooms.get(roomPointers[1][1]);
        RoomStruct exit = rooms.get(roomPointers[ys - 1][xs - 1]);
        start.start = 1;
        exit.exit = 1;

        if(!pathFind(rooms.get(roomPointers[1][1]), 0)){
            log.error("Something happened, we couldn't connect start to exit.");
        }

        //Connect random rooms.
        ArrayList<RoomStruct> branches = new ArrayList<RoomStruct>();
        for(RoomStruct room : rooms) {
            if (room.entered == 1) branches.add(room);
        }
        for(int i = 0; i < branches.size(); i++){
            RoomStruct room = branches.get(i);
            ArrayList<RoomNeighbor> r = adjacentRooms(room);
            Collections.shuffle(r);
            for(RoomNeighbor n : r) {
                RoomStruct targ = rooms.get(roomPointers[n.y][n.x]);
                if(targ.entered == 0 && random.nextFloat() <= branchChance){
                    connect(room, targ);
                    targ.entered = 1;
                    branches.add(targ);
                    break;
                }
            }
        }
        floodRooms();

        //Clean up
        roomPointers = null;
        rooms = null;
    }
}
