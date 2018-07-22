package ure.areas;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import ure.examplegame.ExampleCaveScaper;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.terrain.Stairs;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;
import ure.ui.modals.UModalLoading;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * UCartographer implements a central authority for determining where inter-area exits go.  It defines
 * the overall map of your game.
 *
 * It does this by working closely with the Stairs terrain.  When a Stairs is activated, its label,
 * a simple String, is given to your cartographer.getArea() to receive a new Area to move the player
 * into.
 *
 * By convention this label is of the format 'maptype a[,b,c..]' where a,b,c are arbitrary parameters
 * used by the cartographer to route.  The simplest system is to simply attach one number representing
 * a depth, and generate harder levels for higher depths.  The GetLabelName() / GetLabelData() methods
 * are provided to facilitate this convention.
 *
 */
// TODO: split custom stuff into ExampleCartographer and make generic

public class UCartographer implements Runnable {

    @Inject
    protected UCommander commander;
    @Inject
    protected UTerrainCzar terrainCzar;
    @Inject
    protected UThingCzar thingCzar;
    @Inject
    protected UActorCzar actorCzar;
    @Inject
    protected ObjectMapper objectMapper;

    protected ArrayList<UArea> activeAreas = new ArrayList<>();
    protected ArrayList<UArea> closeableAreas = new ArrayList<>();
    protected HashMap<String,URegion> regions = new HashMap<>();
    protected String startArea;

    LinkedBlockingQueue<String> loadQueue;
    LinkedBlockingQueue<UArea> saveQueue;
    String loadingArea;
    UArea  savingArea;
    Thread loaderThread;

    public boolean waitingForLoad;

    public UCartographer() {
        Injector.getAppComponent().inject(this);
        activeAreas = new ArrayList<UArea>();
        closeableAreas = new ArrayList<UArea>();
        regions = new HashMap<>();
        loadQueue = new LinkedBlockingQueue<>();
        saveQueue = new LinkedBlockingQueue<>();

    }

    /**
     * Do what is necessary to initialize the regions this cartographer will start with.
     */
    public void setupRegions() {
        String savePath = commander.savePath();
        File f = new File(savePath);
        if (!f.isDirectory()) {
            f.mkdir();
        }
        if (commander.config.isPersistentAreas()) {
            loadRegions();
        }
    }

    public void wipeWorld() {
        String savePath = commander.savePath();
        try {
            FileUtils.cleanDirectory(new File(savePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        regions = new HashMap<>();
        setupRegions();
    }
    /**
     * Load all regions that have been persisted to disk.
     */
    public void loadRegions() {
        String path = commander.savePath();
        File dir = new File(path + ".");
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".region");
            }
        });
        if (files != null) {
            for (File file : files) {
                URegion region = loadRegion(file);
                regions.put(region.getId(), region);
            }
        }
    }

    /**
     * Load a single region from disk.
     * @param file
     * @return the region
     */
    protected URegion loadRegion(File file) {
        try (
                FileInputStream stream = new FileInputStream(file);
                GZIPInputStream gzip = new GZIPInputStream(stream)
        ) {
            return objectMapper.readValue(gzip, URegion.class);
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't load region at " + file.getPath(), e);
        }
    }

    public synchronized void startLoader() {
        if (loaderThread == null) {
            loaderThread = new Thread(this);
            loaderThread.start();
        } else if (!loaderThread.isAlive()) {
            loaderThread.start();
        }
    }
    /**
     * Fetch the UArea corresponding to a label.  This looks for a persisted level, or
     * creates a new one if needed.  You probably shouldn't override this.
     *
     * @param label The label from the Stairs leading to the desired area.
     * @return A new or existing UArea.
     */
    public UArea getArea(String label) {
        // First check for active areas
        for (UArea area : activeAreas)
            if (area.label.equals(label))
                return area;

        String labelname = GetLabelName(label);
        int labeldata = GetLabelData(label);
        addAreaToLoadQueue(label);
        waitingForLoad = true;
        //commander.printScroll("Loading...");
        if (commander.modalCamera() != null) {
            commander.showModal(new UModalLoading());
            commander.printScroll("Loading...");
        }
        while (!areaIsActive(label)) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        waitingForLoad = false;
        if (commander.modalCamera() != null)
            commander.detachModal();
        return activeAreaNamed(label);
    }

    /**
     * Fetch the starting area for a new player.
     * By default, if we have regions, pick the first region and return its first level.
     */

    public String getStartArea() { return startArea; }
    public UArea makeStartArea() {
        return getArea(startArea);
    }

    /**
     * Get the name of the current world.  By default this is used to name the savestate file.
     * @return
     */
    public String worldName() {
        return null;
    }

    public void setStartArea(String label) {
        startArea = label;
    }

    /**
     * Fetch the given area from persisted storage, if we have it.
     * If we still have an Area for this label loaded and active, return that.
     *
     * @param label
     * @return null if no persisted area found.
     */
    protected UArea FetchArea(String label, String labelname, int labeldata) {

        // First check for active areas
        for (UArea area : activeAreas)
            if (area.label.equals(label))
                return area;

        if (commander.config.isPersistentAreas()) {
            String path = commander.savePath();
            File file = new File(path + label + ".area");
            try (
                FileInputStream stream = new FileInputStream(file);
                GZIPInputStream gzip = new GZIPInputStream(stream)
            ) {
                UArea area = objectMapper.readValue(gzip, UArea.class);
                area.reconnect();
                area.setLinks();
                return area;
            }
            catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    System.out.println("CARTO: no save found for " + label);
                } else {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Persist an object to disk.  This will most likely be an area or region, but in theory you could
     * write anything that serializes properly.
     * @param object
     * @param filename
     */
    protected void persist(Object object, String filename) {
        String path = commander.savePath();
        if (commander.config.isPersistentAreas()) {
            System.out.println("CARTO : saving file " + path + filename);
            File file = new File(path + filename);
            try (
                    FileOutputStream stream = new FileOutputStream(file);
                    GZIPOutputStream gzip = new GZIPOutputStream(stream)
            ) {
                JsonFactory jfactory = new JsonFactory();
                JsonGenerator jGenerator = jfactory
                        .createGenerator(gzip, JsonEncoding.UTF8);
                jGenerator.setCodec(objectMapper);
                jGenerator.writeObject(object);
                jGenerator.close();
            } catch (IOException e) {
                throw new RuntimeException("Couldn't persist object " + object.toString(), e);
            }
        }
    }

    /**
     * Create a UArea specified by the given label in whatever format we define.
     *
     * By default, find a region with this label's id and ask it for the area.
     *
     */
     public UArea makeArea(String label, String labelname, int labeldata) {
         System.out.println("CARTO: make area for " + labelname + " (" + Integer.toString(labeldata) + ")");
         if (regions.containsKey(labelname)) {
             UArea area = regions.get(labelname).makeArea(labeldata, label);
             area.setLabel(label);
             area.setLinks();
             return area;
         }
         return null;
    }

    /**
     * Add a region to the world.  This lets the carto spawn areas for that region's label id.
     */
    public void addRegion(URegion region) {
         System.out.println("CARTO : adding region " + region.getId());
         persist(region, region.getId() + ".region");
         regions.put(region.getId(), region);
    }

    /**
     * Shut down an active Area and serialize it to disk.
     * After this method there should be no refs to that Area object or anything in it, anywhere.
     *
     * @param area
     */
    void freezeArea(UArea area) {
        if (commander.player().area() == area) {
            System.out.println("ERROR: attempted to freeze player's current area!");
        } else if (area.closed) {
            System.out.println("CARTO LOADER: WARNING - tried to freeze " + area.label + " which is already frozen");
            removeActiveArea(area);
            commander.unregisterTimeListener(area);
        } else if (!activeAreas.contains(area)) {
            System.out.println("ERROR: attempted to freeze an area not in activeAreas!  Where'd that come from?");
        } else {
            area.freezeForPersist();
            removeActiveArea(area);
            commander.unregisterTimeListener(area);
            persist(area, area.getLabel() + ".area");
            addCloseableArea(area);
            area.requestCloseOut();
        }
    }

    /**
     * Extract the 'region' part of the label string, assuming the conventional format (anything before a space).
     *
     * For 'dungeon 47', this would return 'dungeon'.
     * For 'start', this would return 'start'.
     * @param label
     * @return
     */
    public String GetLabelName(String label) {
        int i = label.indexOf(" ");
        if (i < 0) return label;
        return label.substring(0,i);
    }

    /**
     * Extract the 'data' part of the label string.  Usually this indicates the area level within its region.
     *
     * @param label
     * @return An array of integers extracted.
     */
    public int GetLabelData(String label) {
        int di = 0;
        int data = 0;
        int i = label.indexOf(" ");
        if (i >= label.length()-1) return data;
        if (i < 1) return data;
        int lc = i;
        return Integer.parseInt(label.substring(lc+1));
    }

    /**
     * Player has left an area -- check and see if we need to serialize anything or preemptively make
     * new areas.
     *
     * @param player
     * @param area
     */
    public void playerLeftArea(UPlayer player, UArea area) {
        if (!commander.config.isRunNeighborAreas() && area != null)
            freezeArea(area);
        if (commander.config.isLoadAreasAhead()) {
            UArea newArea = player.area();
            for (Stairs stair : newArea.stairsLinks()) {
                String nextArea = stair.getLabel();
                addAreaToLoadQueue(nextArea);
            }
        }
    }

    /**
     * Get a human-readable text title for the area with this label.
     *
     * @param label
     * @return
     */
    public String describeLabel(String label) {
        if (label.equals("vaulted"))
            return "VaultEd";
        String labelname = GetLabelName(label);
        int labeldata = GetLabelData(label);
        if (regions.containsKey(labelname))
            return regions.get(labelname).describeLabel(label, labelname, labeldata);
        return "plane of chaos";
    }

    /**
     * Generate a title screen area.  This is a real area, but it's just for show. :)
     *
     */
    public UArea getTitleArea() {
        UArea area = new UArea(100,100,"floor");
        area.label = "TITLE";
        addActiveArea(area);
        commander.config.addDefaultSunCycle(area);
        return area;
    }

    public synchronized void addActiveArea(UArea area) {
        if (!activeAreas.contains(area))
            activeAreas.add(area);
    }
    public synchronized void removeActiveArea(UArea area) {
        activeAreas.remove(area);
    }
    public synchronized UArea getActiveAreaAt(int i) {
        return activeAreas.get(i);
    }

    public synchronized void addCloseableArea(UArea area) {
        if (!closeableAreas.contains(area))
            closeableAreas.add(area);
    }
    public synchronized void removeCloseableArea(UArea area) {
        closeableAreas.remove(area);
    }
    public boolean areaIsCloseable(UArea area) {
        if (System.nanoTime() - area.closeRequestedTime > (1000*300))
            return true;
        return false;
    }
    public synchronized boolean areaIsActive(String label) {
        for (UArea area : activeAreas)
            if (area.label != null)
                if (area.label.equals(label))
                    return true;
        return false;
    }
    public synchronized UArea activeAreaNamed(String label) {
        for (UArea area : activeAreas)
            if (area.label.equals(label))
                return area;
        return null;
    }
    public synchronized void addAreaToLoadQueue(String label) {
        if (label.equals(loadingArea)) {
            return;
        }
        if (!loadQueue.contains(label))
            loadQueue.add(label);
    }
    public synchronized void addAreaToSaveQueue(UArea area) {
        if (area == savingArea) {
            return;
        }
        if (!saveQueue.contains(area))
            saveQueue.add(area);
    }
    /**
     * This runs in a background thread and services the area load/save queues.
     *
     */
    public void run() {
        System.out.println("CARTO LOADER: background thread starting");
        while (!commander.isQuitGame() && (commander.player() != null) ||
                (!loadQueue.isEmpty() || !saveQueue.isEmpty())) {
            while (!loadQueue.isEmpty()) {
                String next = null;
                try {
                    next = loadQueue.take();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("CARTO LOADER: fetching area " + next + " for queue");
                loadingArea = next;
                String nextname = GetLabelName(next);
                int nextdata = GetLabelData(next);
                UArea area = FetchArea(next, nextname, nextdata);
                if (area == null) {
                    System.out.println("CARTO LOADER:  tried to fetch " + next + " and got null, creating");
                    area = makeArea(next, nextname, nextdata);

                    if (area == null) {
                        System.out.println("CARTO LOADER : ***FAIL*** to make area " + next);
                    } else {
                        persist(area, area.label + ".area");
                    }
                }
                addActiveArea(area);
                commander.registerTimeListener(area);
                loadingArea = null;
            }
            while (!saveQueue.isEmpty()) {
                UArea area = null;
                try {
                    area = saveQueue.take();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("CARTO LOADER: saving area " + area.label + " from queue");
                savingArea = area;
                freezeArea(area);
                savingArea = null;
            }
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
            UArea playerArea = null;
            if (commander.player() != null)
                playerArea = commander.player().area();
            for (int i=0;i<activeAreas.size();i++) {
                UArea area = getActiveAreaAt(i);
                if (area == null)
                    System.out.println("******* NULL AREA IN ACTIVEAREAS");
                if (area != playerArea) {
                    if (playerArea == null) {
                        System.out.println("CARTO LOADER: found area " + area.label + " + to harvest and freeze, player has left the world");
                        addAreaToSaveQueue(area);
                    } else if (area.findExitTo(playerArea.getLabel()) == null) {
                        System.out.println("CARTO LOADER: found area " + area.label + " to harvest and freeze");
                        addAreaToSaveQueue(area);
                    }
                }
            }
            if (!closeableAreas.isEmpty()) {
                ArrayList<UArea> tempCloseable = (ArrayList<UArea>) closeableAreas.clone();
                for (UArea area : tempCloseable) {
                    if (area != null) {
                        if (areaIsCloseable(area)) {
                            area.closeOut();
                            removeCloseableArea(area);
                        }
                    }
                }
            }
        }
        System.out.println("CARTO LOADER: game quit detected, shutting down background thread");
    }

    /**
     * Load and create a VaultSet from a serialized json file.
     */
    public UVaultSet loadVaultSet(String filename) {
        File file = new File(commander.config.getResourcePath() + "vaults/" + filename + ".json");
        try (
                FileInputStream stream = new FileInputStream(file);
                //GZIPInputStream gzip = new GZIPInputStream(stream);
        ) {
            UVaultSet vaultSet = objectMapper.readValue(stream, UVaultSet.class);
            vaultSet.setObjectMapper(objectMapper);
            return vaultSet;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
