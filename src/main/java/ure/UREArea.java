package ure;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    private int xsize, ysize;
    private HashSet<URELight> lights;
    private HashSet<URECamera> cameras;
    private HashSet<UREActor> actors;
    private HashSet<UParticle> particles;
    private URECommander commander;

    public UColor sunColor;
    HashMap<Integer,UColor> sunColorLerps;

    public UREArea(int thexsize, int theysize) {
        xsize = thexsize;
        ysize = theysize;
        for (int i=0;i<xsize;i++) {
            for (int j=0;j<ysize;j++)
                cells[i][j] = new UCell(this, i, j, null);
        }
        initLists();
    }

    public UREArea(String filename, URETerrainCzar terrainCzar) {
        initLists();
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
        sunColorLerps = new HashMap<Integer,UColor>();
        sunColor = new UColor(130,50,25);
        sunColorLerps.put(0, new UColor(0f, 0f, 0.1f));
        sunColorLerps.put(4*3600, new UColor(0.2f, 0.2f, 0.2f));
        sunColorLerps.put(6*3600, new UColor(0.6f, 0.4f, 0.25f));
        sunColorLerps.put(10*3600, new UColor(0.9f, 0.8f, 0.75f));
        sunColorLerps.put(14*3600, new UColor(1f, 1f, 1f));
        sunColorLerps.put(16*3600, new UColor(0.9f, 0.9f, 1f));
        sunColorLerps.put(19*3600, new UColor(0.7f, 0.7f, 0.8f));
        sunColorLerps.put(20*3600, new UColor(0.8f, 0.4f, 0.3f));
        sunColorLerps.put(21*3600, new UColor(0.3f, 0.3f, 0.4f));
        sunColorLerps.put(24*3600, new UColor(0f, 0f, 0.1f));
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

    public void setSunColor(float r, float g, float b) {
        this.sunColor.set(r, g, b);
    }
    public void setSunColor(int r, int g, int b) {
        this.sunColor.set(r,g,b);
    }

    public void setSunColor(int minutes) {
        UColor lerp1, lerp2;
        int min1, min2;
        min1 = 0;
        min2 = 0;
        lerp1 = null;
        lerp2 = null;
        boolean gotem = false;
        for (Integer min : sunColorLerps.keySet()) {
            if (minutes >= min) {
                lerp1 = sunColorLerps.get(min);
                min1 = (int)min;
                gotem = true;
            } else if (gotem) {
                lerp2 = sunColorLerps.get(min);
                min2 = (int)min;
                gotem = false;
            }
        }
        float ratio = (float)minutes / (float)(min2 - min1);
        setSunColor(lerp1.fR() + (lerp2.fR() - lerp1.fR()) * ratio,
                     lerp1.fG() + (lerp2.fG() - lerp1.fG()) * ratio,
                         lerp1.fB() + (lerp2.fB() - lerp1.fB()) * ratio);
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
                return cells[x][y].getTerrain();
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
        return cells[x][y];
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

    public void hearTick(URECommander commander) {
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

    public void redrawCell(int x, int y) {
        Iterator<URECamera> camI = cameras.iterator();
        while (camI.hasNext()) {
            URECamera camera = camI.next();
            camera.redrawAreaCell(x,y);
        }
    }
    public void addParticle(UParticle particle) {
        particles.add(particle);
    }
    public void fizzleParticle(UParticle particle) {
        particles.remove(particle);
    }
}
