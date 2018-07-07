package ure;

import ure.actors.UActor;
import ure.terrain.Stairs;
import ure.terrain.UTerrain;
import ure.terrain.UTerrainCzar;
import ure.things.UThing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by gilmore on 6/18/2018.
 *
 * A self-contained full play map.
 *
 */

public class UArea implements UTimeListener {

    public interface Listener {
        void areaChanged();
    }

    public String label;

    private UCell cells[][];
    public int xsize, ysize;
    private HashSet<ULight> lights;
    private HashSet<UActor> actors;
    private HashSet<UParticle> particles;
    private UCommander commander;
    private UTerrainCzar terrainCzar;
    private Random random;

    public UColor sunColor;

    ArrayList<Integer> sunColorLerpMarkers;
    ArrayList<UColor> sunColorLerps;
    HashMap<Integer,String> sunCycleMessages;
    int sunCycleLastAnnounceMarker;

    private Set<Listener> listeners;

    public UArea(int thexsize, int theysize, UTerrainCzar tczar, String defaultTerrain) {
        xsize = thexsize;
        ysize = theysize;
        terrainCzar = tczar;
        cells = new UCell[xsize][ysize];
        for (int i=0;i<xsize;i++) {
            for (int j=0;j<ysize;j++)
                cells[i][j] = new UCell(this, i, j, terrainCzar.getTerrainByName(defaultTerrain));
        }
        initLists();
    }

    public UArea(String filename, UTerrainCzar tczar) {
        initLists();
        terrainCzar = tczar;
        cells = new UCell[200][200];
        InputStream in = getClass().getResourceAsStream(filename);
        Stream<String> lines = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines();
        lines.forEach(line -> {
            int cellsX = 0;
            for (char c : line.toCharArray()) {
                UTerrain terrain = terrainCzar.getTerrainForFilechar(c);
                cells[cellsX][ysize] = new UCell(this, cellsX, ysize, terrain);
                ++cellsX;
            }
            ysize++;
            xsize = cellsX;
        });
        lines.close();
    }

    void initLists() {
        random = new Random();
        lights = new HashSet<>();
        listeners = new HashSet<>();
        actors = new HashSet<>();
        particles = new HashSet<>();
        sunColorLerps = new ArrayList<>();
        sunColorLerpMarkers = new ArrayList<>();
        sunCycleMessages = new HashMap<>();
        sunColor = new UColor(130,50,25);
        addSunColorLerp(0, new UColor(0.1f, 0.1f, 0.3f));
        addSunColorLerp(4*60, new UColor(0.2f, 0.2f, 0.3f));
        addSunColorLerp(6*60, new UColor(0.9f, 0.5f, 0.35f), "The sun's first rays appear on the horizon.");
        addSunColorLerp(9*60, new UColor(1f, 0.9f, 0.75f));
        addSunColorLerp(13*60, new UColor(1f, 1f, 1f));
        addSunColorLerp(17*60, new UColor(0.9f, 0.9f, 1f));
        addSunColorLerp(19*60, new UColor(0.7f, 0.7f, 0.8f));
        addSunColorLerp(20*60, new UColor(0.8f, 0.4f, 0.3f));
        addSunColorLerp(21*60, new UColor(0.3f, 0.3f, 0.4f), "The sun sets.");
        addSunColorLerp(24*60, new UColor(0.1f, 0.1f, 0.3f));
    }

    public void close() {
        lights = null;
        listeners = null;
        actors = null;
        cells = null;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void setCommander(UCommander cmdr) {
        commander = cmdr;
    }
    public UCommander commander() {
        return commander;
    }

    public HashSet<ULight> lights() {
        return lights;
    }

    void addLight(ULight light) {
        lights.add(light);
    }

    void removeLight(ULight light) {
        lights.remove(light);
    }

    public void addSunColorLerp(int minutes, UColor color) { addSunColorLerp(minutes, color, null); }
    public void addSunColorLerp(int minutes, UColor color, String msg) {
        sunColorLerps.add(color);
        sunColorLerpMarkers.add(minutes);
        if (msg != null) {
            sunCycleMessages.put((Integer)minutes, msg);
        }
    }
    public void setSunColor(float r, float g, float b) {
        this.sunColor.set(r, g, b);
    }

    public void setSunColor(int minutes) {
        UColor lerp1, lerp2;
        int min1, min2;
        min1 = 0;
        min2 = 0;
        lerp1 = null;
        lerp2 = null;
        boolean gotem = false;
        for (int x=0;x<sunColorLerps.size();x++) {
            int min = (int)sunColorLerpMarkers.get(x);
            if (minutes >= min) {
                lerp1 = sunColorLerps.get(x);
                min1 = min;
                gotem = true;
            } else if (gotem) {
                lerp2 = sunColorLerps.get(x);
                min2 = min;
                gotem = false;
            }
        }
        float ratio = (float)(minutes - min1) / (float)(min2 - min1);
        setSunColor(lerp1.fR() + (lerp2.fR() - lerp1.fR()) * ratio,
                     lerp1.fG() + (lerp2.fG() - lerp1.fG()) * ratio,
                         lerp1.fB() + (lerp2.fB() - lerp1.fB()) * ratio);
        String msg = sunCycleMessages.get(min1);
        if (msg != null && commander.player() != null) {
            if (sunCycleLastAnnounceMarker != min1) {
                sunCycleLastAnnounceMarker = min1;
                if (cells[commander.player().areaX()][commander.player().areaY()].sunBrightness > 0.1f)
                    commander.printScroll(msg);
            }
        }
    }

    public boolean isValidXY(int x, int y) {
        if ((x >= 0) && (y >= 0))
            if ((x < xsize) && (y < ysize))
                return true;
        return false;
    }

    public UTerrain terrainAt(int x, int y) {
        if (isValidXY(x, y))
            if (cells[x][y] != null)
                return cells[x][y].terrain();
        return null;
    }

    public void setTerrain(int x, int y, String t) {
        if (isValidXY(x, y)) {
            UTerrain terrain = terrainCzar.getTerrainByName(t);
            cells[x][y].useTerrain(terrain);
        }
    }

    public UCell cellAt(int x, int y) {
        if (isValidXY(x,y)) {
            return cells[x][y];
        }
        return null;
    }

    public boolean blocksLight(int x, int y) {
        UTerrain t = terrainAt(x, y);
        if (t != null)
            return t.isOpaque();
        return true;
    }

    public void setSeen(int x, int y) {
        setSeen(x, y, true);
    }

    public void setSeen(int x, int y, boolean seen) {
        if (isValidXY(x, y))
            cells[x][y].setSeen(seen);

    }

    public boolean seenCell(int x, int y) {
        if (isValidXY(x, y))
            return cells[x][y].isSeen();
        return false;
    }

    public float sunBrightnessAt(int x, int y) {
        if (isValidXY(x,y))
            if (cells[x][y] != null)
                return cells[x][y].sunBrightness();
        return 0.0f;
    }

    public boolean willAcceptThing(UThing thing, int x, int y) {
        if (isValidXY(x, y))
            return cells[x][y].willAcceptThing(thing);
        return false;
    }

    public UCell addThing(UThing thing, int x, int y) {
        cells[x][y].addThing(thing);
        if (thing.isActor()) {
            actors.add((UActor) thing);
            if (((UActor) thing).isPlayer()) {
                wakeCheckAll(x,y);
            } else {
                ((UActor)thing).wakeCheck(x,y);
            }
        }
        return cells[x][y];
    }

    private void wakeCheckAll(int playerx, int playery) {
        for (UActor actor : actors) {
            actor.wakeCheck(playerx, playery);
        }
    }

    public UCell randomOpenCell(UThing thing) {
        UCell cell = null;
        boolean match = false;
        while (cell == null || !match) {
            cell = cellAt(random.nextInt(xsize), random.nextInt(ysize));
            match = cell.willAcceptThing(thing);
        }
        return cell;
    }

    public void hearRemoveThing(UThing thing) {
        actors.remove(thing);
    }

    public Iterator<UThing> thingsAt(int x, int y) {
        if (isValidXY(x,y)) {
            return cells[x][y].iterator();
        }
        return null;
    }
    public UActor actorAt(int x, int y) {
        if (isValidXY(x,y)) {
            return cells[x][y].actorAt();
        }
        return null;
    }

    public UCell findExitTo(String label) {
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                if (cells[x][y].terrain() instanceof Stairs) {
                    if (((Stairs)cells[x][y].terrain()).label() == label) {
                        return cells[x][y];
                    }
                }
            }
        }
        return null;
    }

    public void hearTimeTick(UCommander commander) {
        System.out.println(label + " tick");
        setSunColor(commander.daytimeMinutes());
        updateListeners();
    }

    void updateListeners() {
        for (Listener listener : listeners) {
            listener.areaChanged();
        }
    }

    public void setLabel(String thelabel) {
        label = thelabel;
    }
    public void addParticle(UParticle particle) {
        particles.add(particle);
    }
    public void fizzleParticle(UParticle particle) {
        particles.remove(particle);
    }
}
