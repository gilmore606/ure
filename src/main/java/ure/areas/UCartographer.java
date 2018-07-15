package ure.areas;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actors.UActorCzar;
import ure.actors.UPlayer;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

public class UCartographer {

    @Inject
    protected UTerrainCzar terrainCzar;
    @Inject
    protected UThingCzar thingCzar;
    @Inject
    protected UActorCzar actorCzar;
    @Inject
    protected ObjectMapper objectMapper;

    UCommander commander;
    ArrayList<UArea> activeAreas;

    public UCartographer() {
        Injector.getAppComponent().inject(this);
        activeAreas = new ArrayList<UArea>();
    }

    public void setCommander(UCommander cmdr) {
        commander = cmdr;
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
        int[] labeldata = GetLabelData(label);

        area = FetchArea(label, labelname, labeldata);
        if (area != null)
            return area;

        area = makeArea(label, labelname, labeldata);
        area.setLabel(label);
        activeAreas.add(area);
        commander.registerTimeListener(area);
        return area;
    }

    /**
     * Fetch the given area from persisted storage, if we have it.
     * If we still have an Area for this label loaded and active, return that.
     *
     * @param label
     * @return null if no persisted area found.
     */
    UArea FetchArea(String label, String labelname, int[] labeldata) {
        if (!commander.config.isPersistentAreas())
            return null;
        return null;
    }

    /**
     * Persist an area to disk.
     *
     * This doesn't work yet because there are a lot of things to sort out with regard to
     * what actually gets serialized.
     */
    protected void persistArea(UArea area, String filename) {
        if (!commander.config.isPersistentAreas())
            return;
        File areaFile = new File(filename);
        try {
            objectMapper.writeValue(areaFile, area);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't persist area", e);
        }
    }

    /**
     * Create a UArea specified by the given label in whatever format we define.
     *
     * The default implementation does nothing; you need to override this and generate areas for your game.
     *
     * Override this to select among your own master set of ULandscapers.
     *
     */
     public UArea makeArea(String label, String labelname, int[] labeldata) {
        return null;
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
            activeAreas.remove(area);
            commander.unregisterTimeListener(area);
            // TODO: actually serialize the area now
            // TODO: somehow deref everything in the area so it gets GC'd?  that'd be nice eh.
            boolean serializationWorksNow = false;
            if (serializationWorksNow) {
                persistArea(area, "uarea-" + area.getLabel());
            } else {
                System.out.println("I would have serialized " + area.getLabel() + " at this point.");
            }
        }
    }

    /**
     * Extract the 'maptype' part of the label string, assuming the conventional format (anything before a space).
     *
     * For 'dungeon 47', this would return 'dungeon'.
     * For 'start', this would return 'start'.
     * @param label
     * @return
     */
    public String GetLabelName(String label) {
        int i = label.indexOf(" ");
        if (i < 1) return label;
        return label.substring(0,i);
    }

    /**
     * Extract the 'data' part of the label string, assuming the conventional format (a comma-separated
     * list of integers after the space).
     *
     * @param label
     * @return An array of integers extracted.
     */
    public int[] GetLabelData(String label) {
        int di = 0;
        int[] data = new int[20];
        int i = label.indexOf(" ");
        if (i >= label.length()-1) return data;
        if (i < 1) return data;
        int lc = i;
        System.out.println(label);
        for (;i<=label.length();i++) {
            if (i == label.length()) {
                if (lc < (i-2))
                    data[di] = Integer.parseInt(label.substring(lc+1,i-1));
            } else if (label.charAt(i) == ',') {
                data[di] = Integer.parseInt(label.substring(lc+1,i-1));
                lc = i;
                di++;
            }
        }
        return data;
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
        return "plane of chaos";
    }



}
