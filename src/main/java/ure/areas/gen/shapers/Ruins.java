package ure.areas.gen.shapers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.areas.UArea;
import ure.areas.gen.Layer;

import java.util.ArrayList;

public class Ruins extends Shaper {

    public static final String TYPE = "Ruins";

    @JsonIgnore
    private Log log = LogFactory.getLog(Ruins.class);

    public Ruins() { super(TYPE); }

    @Override
    public void setupParams() {
        addParamI("roomsizeMin", 1, 4, 10);
        addParamI("roomsizeMax", 2, 10, 20);
        addParamI("minroomarea", 4, 12, 50);
        addParamI("roomsmax", 1, 20, 200);
        addParamI("hallwidth", 1, 3, 5);
        addParamF("hallChance", 0f, 0.3f, 1f);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildRuins(getParamI("roomsizeMin"),getParamI("roomsizeMax"),getParamI("minroomarea"),getParamI("roomsmax"),getParamI("hallwidth"),getParamF("hallChance"));
    }

    public void buildRuins(int roomsizeMin, int roomsizeMax, int minroomarea, int roomsmax, int hallwidth, float hallChance) {
        clear();
        ArrayList<int[]> rooms = new ArrayList<>();
        int firstw = random.i(roomsizeMin,roomsizeMax);
        int firsth = random.i(roomsizeMin,roomsizeMax);
        int firstx = xsize / 2;
        int firsty = ysize / 2;
        buildRoom(firstx,firsty,firstw,firsth);
        rooms.add(new int[]{firstx,firsty,firstw,firsth});
        boolean done = false;
        int fails = 0;
        while (!done && (fails < rooms.size()*6) && (rooms.size() < roomsmax)) {
            int[] sourceroom = rooms.get(random.i(rooms.size()));
            int wallid = random.i(4);
            int dx, dy, sx, sy;
            if (wallid == 0) {
                dx = 0; dy = -1;
                sx = sourceroom[0]; sy = sourceroom[1];
            } else if (wallid == 1) {
                dx = 1; dy = 0;
                sx = sourceroom[0] + sourceroom[2] - 1; sy = sourceroom[1];
            } else if (wallid == 2) {
                dx = 0; dy = 1;
                sx = sourceroom[0]; sy = sourceroom[1] + sourceroom[3] - 1;
            } else {
                dx = -1; dy = 0;
                sx = sourceroom[0]; sy = sourceroom[1];
            }
            int[] newbox = new int[]{0,0};
            if (random.f() < hallChance) {
                newbox[0] = hallwidth;
                newbox[1] = random.i(roomsizeMin,roomsizeMax);
            } else {
                while (newbox[0] * newbox[1] < minroomarea) {
                    newbox[0] = random.i(roomsizeMin,roomsizeMax);
                    newbox[1] = random.i(roomsizeMin,roomsizeMax);
                }
            }
            if ((sourceroom[2] > sourceroom[3] && newbox[0] > newbox[1]) || (sourceroom[2] < sourceroom[3] && newbox[0] < newbox[1])) {
                int tmp = newbox[0];
                newbox[0] = newbox[1];
                newbox[1] = tmp;
            }
            int slidemin, slidemax;
            if (dx == 0) {
                slidemin = (0 - newbox[0]) + 3;
                slidemax = (sourceroom[2]) - 3;
            } else {
                slidemin = (0 - newbox[1]) + 3;
                slidemax = (sourceroom[3]) - 3;
            }
            int[][] connectpoints = new int[(slidemax-slidemin)+2][2];  int connecti = 0;
            int[] slidepos = new int[2];
            int[] newroom = new int[4];
            for (int slide=slidemin;slide<slidemax;slide++) {
                slidepos[0] = (sx + slide * Math.abs(dy));
                slidepos[1] = (sy + slide * Math.abs(dx));
                int newroomtest[] = new int[]{slidepos[0]+dx,slidepos[1]+dy,newbox[0]-Math.abs(dx),newbox[1]-Math.abs(dy)};
                newroom = new int[]{slidepos[0],slidepos[1],newbox[0],newbox[1]};
                if (dx<0) {
                    newroomtest[0] = 1 + slidepos[0] - newbox[0];
                    newroom[0] = 1 + slidepos[0] - newbox[0];
                }
                if (dy<0) {
                    newroomtest[1] = 1 + slidepos[1] - newbox[1];
                    newroom[1] = 1 + slidepos[1] - newbox[1];
                }
                if (canFitBoxAt(newroomtest[0],newroomtest[1],newroomtest[2],newroomtest[3])) {
                    if (newroomtest[0] >= 0 && newroomtest[1] >= 0 && newroomtest[0]+newroomtest[2] <= xsize-1 && newroomtest[1]+newroomtest[3] <= ysize-1) {
                        connectpoints[connecti] = new int[]{slidepos[0], slidepos[1]};
                        connecti++;
                    }
                }
            }
            if (connecti > 0) {
                int[] newloc = connectpoints[random.i(connecti)];
                newroom[0] = newloc[0];
                newroom[1] = newloc[1];
                newroom[2] = newbox[0];
                newroom[3] = newbox[1];
                if (dy == -1) {
                    newroom[1] = newroom[1] - (newbox[1] - 1);
                }
                if (dx == -1) {
                    newroom[0] = newroom[0] - (newbox[0] - 1);
                }
                int doormin = 0, doormax = 0, doorconst = 0;
                if (dy != 0) {
                    doormin = Math.max(sourceroom[0]+1,newroom[0]+1);
                    doormax = Math.min(sourceroom[0]+sourceroom[2]-2,newroom[0]+newroom[2]-2);
                    if (dy == -1) doorconst = sourceroom[1]; else doorconst = sourceroom[1] + sourceroom[3] - 1;
                } else if (dx != 0) {
                    doormin = Math.max(sourceroom[1]+1,newroom[1]+1);
                    doormax = Math.min(sourceroom[1]+sourceroom[3]-2,newroom[1]+newroom[3]-2);
                    if (dx == -1) doorconst = sourceroom[0]; else doorconst = sourceroom[0] + sourceroom[2] - 1;
                }
                buildRoom(newroom[0],newroom[1],newroom[2],newroom[3]);
                rooms.add(newroom);
                int doorstyle = random.i(2);
                if (doorstyle == 0) {
                    int mid = doormin;
                    if (doormax > doormin) mid = random.i(doormin,doormax);
                    if (dy != 0) set(mid, doorconst);
                    else set(doorconst,mid);
                } else if (doorstyle == 1) {
                    for (int i = doormin;i <= doormax;i++) {
                        if (dy != 0) {
                            set(i, doorconst);
                        } else {
                            set(doorconst, i);
                        }
                    }
                }
                log.debug("made new room " + Integer.toString(newroom[2]) + " by " + Integer.toString(newroom[3]));
                fails = 0;
            } else {
                fails++;
            }
        }
    }

    void buildRoom(int x, int y, int w, int h) {
        for (int i=1;i<w-1;i++) {
            for (int j=1;j<h-1;j++) {
                set(x+i,y+j);
            }
        }
    }

    boolean canFitBoxAt(int x, int y, int w, int h) {
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++) {
                if (value(x+i,y+j))
                    return false;
            }
        }
        return true;
    }
}
