package ure.areas.gen.shapers;

import ure.areas.gen.Shape;

import java.util.ArrayList;

public class Mines extends Shaper {

    public class Digger {
        Shape brush, mask;
        float x, y, angle, turnChance;
        boolean done, turning, willConnect;
        int forksteps, turnsteps, turnStepCount, turnDir, minForkSteps, maxForkSteps;

        public Digger(Shape mask, int x, int y, float angle, int width, int minForkSteps, int maxForkSteps, float connectChance, int turnStepCount, float turnChance) {
            brush = new Shape(width, width);
            brush.invert();
            this.mask = mask;
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.minForkSteps = minForkSteps;
            this.maxForkSteps = maxForkSteps;
            if (random.f() < connectChance) {
                this.willConnect = true;
            } else {
                this.willConnect = false;
            }
            this.turnStepCount = turnStepCount;
            this.turnChance = turnChance;
            done = false;
            turning = false;
            forksteps = 0;
            turnsteps = 0;
        }

        public boolean run() {
            if (done) return true;
            mask.maskWith(brush, Shape.MASK_OR, (int) x, (int) y);
            x += Math.cos(angle);
            y += Math.sin(angle);
            if (x < brush.xsize || y < brush.ysize || x > mask.xsize - brush.xsize || y > mask.ysize - brush.ysize) {
                terminate();
            } else {
                int stopDistMin = brush.xsize / 2 + 2;
                int stopDistMax = willConnect ? brush.xsize / 2 + 2 : brush.xsize + minForkSteps;
                for (int dist = stopDistMin;dist <= stopDistMax;dist++) {
                    for (int sweep = -4;sweep <= 4;sweep++) {
                        float sweptAngle = angle + (float) sweep * 0.03f;
                        if (mask.value((int) Math.rint(x + Math.cos(sweptAngle) * (0.5f + dist)), (int) Math.rint(y + Math.sin(sweptAngle) * (0.5f + dist)))) {
                            if (willConnect)
                                mask.maskWith(brush, Shape.MASK_OR, (int) x, (int) y);
                            terminate();
                        }
                    }
                }
            }
            if (turning) {
                turnsteps++;
                angle = angle + (1.5708f / turnStepCount) * turnDir;
                if (turnsteps >= turnStepCount)
                    turning = false;
            } else {
                forksteps++;
            }
            if (!done && !turning && forksteps >= minForkSteps && random.f() < turnChance * (float) (forksteps - minForkSteps) / (float) (maxForkSteps - minForkSteps)) {
                turning = true;
                turnDir = random.f() > 0.5f ? -1 : 1;
                turnsteps = 0;
                forksteps = 0;
            }
            return done;
        }

        void terminate() {
            done = true;
        }

        boolean shouldFork(int minsteps, int maxsteps) {
            if (forksteps <= minsteps) return false;
            if (turning) return false;
            if (random.f() < (float) (forksteps - minsteps) / (float) (maxsteps - minsteps)) {
                forksteps = 0;
                return true;
            }
            return false;
        }
    }

    public Mines(int xsize, int ysize) { super(xsize, ysize); }

    @Override
    void setupParams() {
        addParamI("tunnelWidth", 1, 3, 6);
        addParamI("minForkSteps", 1, 6, 20);
        addParamI("maxForkSteps", 2, 12, 40);
        addParamI("turnStepCount", 1, 7, 15);
        addParamF("turnChance", 0f, 0.4f, 1f);
        addParamF("connectChance", 0f, 0.02f, 1f);
        addParamF("narrowChance", 0f, 0.6f, 1f);
        addParamF("roomChance", 0f, 0.6f, 1f);
        addParamF("backRoomChance", 0f, 1f, 1f);
        addParamI("maxRooms", 0, 100, 400);
        addParamI("roomSizeMin", 2, 3, 15);
        addParamI("roomSizeMax", 2, 12, 30);
    }

    @Override
    public void build() {
        buildMines(getParamI("tunnelWidth"),getParamI("minForkSteps"),getParamI("maxForkSteps"),getParamI("turnStepCount"),getParamF("turnChance"),getParamF("connectChance"),getParamF("narrowChance"),getParamF("roomChance"),getParamF("backRoomChance"),getParamI("maxRooms"),getParamI("roomSizeMin"),getParamI("roomSizeMax"));
    }

    void buildMines(int tunnelWidth, int minForkSteps, int maxForkSteps, int turnStepCount, float turnChance, float connectChance, float narrowChance, float roomChance, float backRoomChance, int maxRooms, int roomSizeMin, int roomSizeMax) {
        clear();
        ArrayList<Room> spareRooms = new ArrayList<>();
        for (int i=0;i<maxRooms;i++) {
            spareRooms.add(new Room(0, 0,random.i(roomSizeMin,roomSizeMax), random.i(roomSizeMin,roomSizeMax)));
        }
        Digger firstDigger = new Digger(this,xsize/2,ysize/2,1.5707f,tunnelWidth, minForkSteps, maxForkSteps, connectChance, turnStepCount, turnChance);
        Digger secondDigger = new Digger(this,xsize/2,ysize/2,-1.5707f, tunnelWidth, minForkSteps, maxForkSteps, connectChance, turnStepCount, turnChance);
        ArrayList<Digger> diggers = new ArrayList<>();
        ArrayList<Digger> tmp;
        diggers.add(firstDigger);
        diggers.add(secondDigger);
        boolean allDone = false;
        while (!allDone) {
            allDone = true;
            tmp = (ArrayList<Digger>)diggers.clone();
            for (Digger digger : tmp) {
                if (!digger.run()) {
                    allDone = false;
                    if (digger.shouldFork(minForkSteps,maxForkSteps) && diggers.size() < 150) {
                        float childAngle = digger.angle + (random.f() < 0.5f ? -1.5708f : 1.5708f);
                        int childWidth = digger.brush.xsize;
                        if (childWidth > 2 && random.f() < narrowChance) childWidth--;
                        Digger child = new Digger(this,(int)digger.x,(int)digger.y,childAngle,childWidth,minForkSteps,maxForkSteps,connectChance, turnStepCount, turnChance);
                        diggers.add(child);
                        if (random.f() < 0.5f) {
                            Digger child2 = new Digger(this,(int)digger.x,(int)digger.y,childAngle+3.1416f,childWidth,minForkSteps,maxForkSteps,connectChance,turnStepCount,turnChance);
                            diggers.add(child2);
                        }
                    } else if (!digger.turning && random.f() < roomChance && !spareRooms.isEmpty()) {
                        Room room = spareRooms.get(random.i(spareRooms.size()));
                        // is there room.w/h space N units toward angle?
                        float roomangle = random.f() < 0.5f ? digger.angle - 1.5708f : digger.angle + 1.5708f;
                        if (tryToFitRoom((int)digger.x,(int)digger.y,room.width,room.height,roomangle,digger.brush.xsize/2+1,true)) {
                            spareRooms.remove(room);
                            if (random.f() < backRoomChance) {
                                Room oldroom = room;
                                room = spareRooms.get(random.i(spareRooms.size()));
                                int offx = (int)Math.rint(digger.x+(digger.brush.xsize/2+oldroom.height/2+1)*Math.cos(roomangle));
                                int offy = (int)Math.rint(digger.y+(digger.brush.xsize/2+oldroom.height/2+1)*Math.sin(roomangle));
                                float backangle = roomangle;
                                if (tryToFitRoom(offx,offy,room.width,room.height,backangle,oldroom.height/2+1,true)) {
                                    spareRooms.remove(room);
                                    set(offx-(int)Math.rint(Math.sin(backangle)),offy-(int)Math.rint(Math.sin(backangle)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
