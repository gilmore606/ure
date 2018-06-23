/**
 * Created by gilmore on 6/18/2018.
 *
 * A self-contained full play map.
 *
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;

public class UREArea implements UTimeListener {

    private UCell cells[][];
    private int xsize, ysize;
    private HashSet<URELight> lights;
    private HashSet<URECamera> cameras;
    private HashSet<UREActor> actors;


    public UREArea(int thexsize, int theysize) {
        xsize = thexsize;
        ysize = theysize;
        for (int i=0;i<xsize;i++) {
            for (int j=0;j<ysize;j++) {
                cells[i][j] = new UCell(this, null);
            }
        }
        initLists();
    }

    public UREArea(String filename, URETerrainCzar terrainCzar) {
        initLists();
        cells = new UCell[200][200];
        try {
            Stream<String> lines = Files.lines(Paths.get(filename));
            lines.forEach(line -> {
                int cellsX = 0;
                for (char c : line.toCharArray()) {
                    URETerrain terrain = terrainCzar.getTerrainForFilechar(c);
                    cells[cellsX][ysize] = new UCell(this, terrain);
                    ++cellsX;
                }
                ysize++;
                xsize = cellsX;
                    });
            lines.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    void initLists() {
        lights = new HashSet<URELight>();
        cameras = new HashSet<URECamera>();
        actors = new HashSet<UREActor>();
    }

    public void close() {
        lights = null;
        cameras = null;
        actors = null;
        cells = null;
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

    public void addThing(UREThing thing, int x, int y) {
        cells[x][y].addThing(thing);
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

    public void hearTick() {
        UpdateCameras();
    }

    void UpdateCameras() {
        Iterator<URECamera> camI = cameras.iterator();
        while (camI.hasNext()) {
            URECamera camera = camI.next();
            camera.renderImage();
        }
    }
}
