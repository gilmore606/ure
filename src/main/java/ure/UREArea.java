package ure;

import ure.actors.UREActor;
import ure.actors.UREPlayer;
import ure.terrain.URETerrain;
import ure.terrain.URETerrainCzar;
import ure.things.UREThing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by gilmore on 6/18/2018.
 *
 * A self-contained full play map.
 *
 */

public class UREArea implements UTimeListener {

    private UCell cells[][];
    public int xsize, ysize;
    private HashSet<URELight> lights;
    private HashSet<URECamera> cameras;
    private HashSet<UREActor> actors;
    private HashSet<UParticle> particles;
    private URECommander commander;
    private URETerrainCzar terrainCzar;

    public UColor sunColor;

    ArrayList<Integer> sunColorLerpMarkers;
    ArrayList<UColor> sunColorLerps;
    HashMap<Integer,String> sunCycleMessages;
    int sunCycleLastAnnounceMarker;

    public UREArea(int thexsize, int theysize, URETerrainCzar tczar, String defaultTerrain) {
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

    public UREArea(String filename, URETerrainCzar tczar) {
        initLists();
        terrainCzar = tczar;
        cells = new UCell[200][200];
        InputStream in = getClass().getResourceAsStream(filename);
        Stream<String> lines = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines();
        lines.forEach(line -> {
            int cellsX = 0;
            for (char c : line.toCharArray()) {
                URETerrain terrain = terrainCzar.getTerrainForFilechar(c);
                cells[cellsX][ysize] = new UCell(this, cellsX, ysize, terrain);
                ++cellsX;
            }
            ysize++;
            xsize = cellsX;
        });
        lines.close();
    }

    void initLists() {
        lights = new HashSet<URELight>();
        cameras = new HashSet<URECamera>();
        actors = new HashSet<UREActor>();
        particles = new HashSet<UParticle>();
        sunColorLerps = new ArrayList<UColor>();
        sunColorLerpMarkers = new ArrayList<Integer>();
        sunCycleMessages = new HashMap<Integer,String>();
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
        cameras = null;
        actors = null;
        cells = null;
    }

    public void setCommander(URECommander cmdr) {
        commander = cmdr;
    }
    public URECommander commander() {
        return commander;
    }

    public void registerCamera(URECamera thecam) {
        cameras.add(thecam);
    }
    public void unRegisterCamera(URECamera thecam) {
        cameras.remove(thecam);
    }

    public HashSet<URELight> lights() {
        return lights;
    }

    void addLight(URELight light) {
        lights.add(light);
    }

    void removeLight(URELight light) {
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

    URETerrain terrainAt(int x, int y) {
        if (isValidXY(x, y))
            if (cells[x][y] != null)
                return cells[x][y].terrain();
        return null;
    }

    public void setTerrain(int x, int y, String t) {
        if (isValidXY(x, y)) {
            URETerrain terrain = terrainCzar.getTerrainByName(t);
            cells[x][y].useTerrain(terrain);
        }
    }

    public UCell cellAt(int x, int y) {
        if (isValidXY(x,y)) {
            return cells[x][y];
        }
        return null;
    }

    boolean blocksLight(int x, int y) {
        URETerrain t = terrainAt(x, y);
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
    float sunBrightnessAt(int x, int y) {
        if (isValidXY(x,y))
            if (cells[x][y] != null)
                return cells[x][y].sunBrightness();
        return 0.0f;
    }

    public boolean willAcceptThing(UREThing thing, int x, int y) {
        if (isValidXY(x, y))
            return cells[x][y].willAcceptThing(thing);
        return false;
    }

    public UCell addThing(UREThing thing, int x, int y) {
        cells[x][y].addThing(thing);
        if (thing.isActor()) {
            actors.add((UREActor) thing);
            if (((UREActor) thing).isPlayer()) {
                wakeCheckAll(x,y);
            } else {
                ((UREActor)thing).wakeCheck(x,y);
            }
        }
        return cells[x][y];
    }

    void wakeCheckAll(int playerx, int playery) {
        for (UREActor actor : actors) {
            actor.wakeCheck(playerx, playery);
        }
    }

    public void hearRemoveThing(UREThing thing) {
        actors.remove(thing);
    }

    public Iterator<UREThing> thingsAt(int x, int y) {
        if (isValidXY(x,y)) {
            return cells[x][y].iterator();
        }
        return null;
    }
    public UREActor actorAt(int x, int y) {
        if (isValidXY(x,y)) {
            return cells[x][y].actorAt();
        }
        return null;
    }

    public void hearTimeTick(URECommander commander) {
        setSunColor(commander.daytimeMinutes());
        UpdateCameras();
    }

    void UpdateCameras() {
        Iterator<URECamera> camI = cameras.iterator();
        while (camI.hasNext()) {
            URECamera camera = camI.next();
            camera.renderImage();
        }
    }

    public void addParticle(UParticle particle) {
        particles.add(particle);
    }
    public void fizzleParticle(UParticle particle) {
        particles.remove(particle);
    }
}
