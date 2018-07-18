package ure.areas;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;

import javax.inject.Inject;
import java.io.*;
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

    ArrayList<UArea> activeAreas;
    HashMap<String,URegion> regions;
    public String startArea;

    LinkedBlockingQueue<String> loadQueue;
    LinkedBlockingQueue<UArea> saveQueue;
    String loadingArea;
    UArea  savingArea;
    Thread loaderThread;

    public UCartographer() {
        Injector.getAppComponent().inject(this);
        activeAreas = new ArrayList<UArea>();
        regions = new HashMap<>();
        loadQueue = new LinkedBlockingQueue<>();
        saveQueue = new LinkedBlockingQueue<>();

    }

    public synchronized void startLoader() {
        if (loaderThread == null) {
            loaderThread = new Thread(this);
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
        UArea area;
        String labelname = GetLabelName(label);
        int labeldata = GetLabelData(label);

        area = FetchArea(label, labelname, labeldata);
        if (area != null)
            return area;

        area = makeArea(label, labelname, labeldata);
        area.setLabel(label);
        addActiveArea(area);
        commander.registerTimeListener(area);
        return area;
    }

    /**
     * Fetch the starting area for a new player.
     * By default, if we have regions, pick the first region and return its first level.
     */

    public UArea getStartArea() {
        return getArea(startArea);
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
            File file = new File(label + ".area");
            try (
                FileInputStream stream = new FileInputStream(file);
                GZIPInputStream gzip = new GZIPInputStream(stream)
            ) {
                UArea area = objectMapper.readValue(gzip, UArea.class);
                area.reconnect();
                addActiveArea(area);
                return area;
            }
            catch (IOException e) {
                System.out.println("Couldn't load area for " + label);
            }
        }

        return null;
    }

    /**
     * Persist an area to disk.
     *
     * This doesn't work yet because there are a lot of things to sort out with regard to
     * what actually gets serialized.
     */
    protected void persistArea(UArea area, String filename) {
        if (commander.config.isPersistentAreas()) {
            File file = new File(filename);
            try (
                FileOutputStream stream = new FileOutputStream(file);
                GZIPOutputStream gzip = new GZIPOutputStream(stream)
            ) {
                JsonFactory jfactory = new JsonFactory();
                JsonGenerator jGenerator = jfactory
                        .createGenerator(gzip, JsonEncoding.UTF8);
                jGenerator.setCodec(objectMapper);
                jGenerator.writeObject(area);
                jGenerator.close();
            } catch (IOException e) {
                throw new RuntimeException("Couldn't persist area", e);
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
             return regions.get(labelname).makeArea(labeldata, label);
         }
         return null;
    }

    /**
     * Add a region to the world.  This lets the carto spawn areas for that region's label id.
     */
    public void addRegion(URegion _region) {
         System.out.println("CARTO : adding region " + _region.id);
         regions.put(_region.id, _region);
    }

    /**
     * Shut down an active Area and serialize it to disk.
     *
     * @param area
     */
    void freezeArea(UArea area) {
        if (commander.player().area() == area) {
            System.out.println("ERROR: attempted to freeze player's current area!");
        } else if (!activeAreas.contains(area)) {
            System.out.println("ERROR: attempted to freeze an area not in activeAreas!  Where'd that come from?");
        } else {
            area.freezeForPersist();
            removeActiveArea(area);
            commander.unregisterTimeListener(area);
            persistArea(area, area.getLabel() + ".area");
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
        // for now we're just gonna immediately freeze that old area
        // TODO: keep old areas around until they're 2 exits away
        freezeArea(area);
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
        return area;
    }

    public synchronized void addActiveArea(UArea area) {
        activeAreas.add(area);
    }
    public synchronized void removeActiveArea(UArea area) {
        activeAreas.remove(area);
    }

    /**
     * This runs in a background thread and services the area load/save queues.
     *
     */
    public void run() {
        System.out.println("CARTO LOADER: background thread starting");
        while (!commander.isQuitGame()) {
            while (!loadQueue.isEmpty()) {
                String next = null;
                try {
                    next = loadQueue.take();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("CARTO LOADER: fetching area " + next + " for queue");
                loadingArea = next;
                UArea area = FetchArea(next, GetLabelName(next), GetLabelData(next));
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
        }
        System.out.println("CARTO LOADER: game quit detected, shutting down background thread");
    }

}
