package ure.areas;

import ure.UCommander;
import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.examplegame.ExampleCaveScaper;
import ure.examplegame.ExampleComplexScaper;
import ure.examplegame.ExampleDungeonScaper;
import ure.examplegame.ExampleForestScaper;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;

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
public class UCartographer {

    UTerrainCzar terrainCzar;
    UThingCzar thingCzar;
    UActorCzar actorCzar;
    UCommander commander;

    public UCartographer(UTerrainCzar theTerrainCzar, UThingCzar theThingCzar, UActorCzar theActorCzar) {
        terrainCzar = theTerrainCzar;
        thingCzar = theThingCzar;
        actorCzar = theActorCzar;
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
        area.setCommander(commander);
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
        return null;
    }

    /**
     * Create a UArea specified by the given label in whatever format we define.
     *
     * The default implementation simply switches between some basic ULandscaper generators
     * with a depth parameter.  This is probably not what you want.
     *
     * Override this to select among your own master set of ULandscapers.
     *
     */
    UArea makeArea(String label, String labelname, int[] labeldata) {
        System.out.println("CARTO : making area for " + labelname + " (" + labeldata[0] + ")");
        UArea area;
        switch (labelname) {
            case "forest":
                area = MakeForest(); break;
            case "cavern":
                area = MakeCavern(labeldata[0]); break;
            case "dungeon":
                area = MakeDungeon(); break;
            case "complex":
                area = MakeComplex(); break;
            default:
                area = MakeForest(); break;

        }
        return area;
    }

    /**
     * Extract the 'maptype' part of the label string, assuming the conventional format (anything before a space).
     *
     * For 'dungeon 47', this would return 'dungeon'.
     * For 'start', this would return 'start'.
     * @param label
     * @return
     */
    String GetLabelName(String label) {
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
    int[] GetLabelData(String label) {
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

    public UArea MakeForest() {
        UArea area = new UArea(140, 140, terrainCzar, "grass");
        area.setCommander(commander);
        ULandscaper scaper = new ExampleForestScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        for (int i=0;i<30;i++) {
            UActor rat = actorCzar.getActorByName("rat");
            UCell ratdest = scaper.randomOpenCell(area, rat);
            rat.moveToCell(area, ratdest.x, ratdest.y);
         }
        return area;
    }

    public UArea MakeComplex() {
        UArea area = new UArea(70, 70, terrainCzar, "grass");
        area.setCommander(commander);
        ULandscaper scaper = new ExampleComplexScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        return area;
    }

    public UArea MakeCavern(int depth) {
        UArea area = new UArea(70, 70, terrainCzar, "wall");
        area.setCommander(commander);
        ULandscaper scaper = new ExampleCaveScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        UActor monk = actorCzar.getActorByName("monk");
        UCell monkdest = scaper.randomOpenCell(area, monk);
        monk.moveToCell(area, monkdest.x, monkdest.y);
        return area;
    }

    public UArea MakeDungeon() {
        UArea area = new UArea(101, 101, terrainCzar, "wall");
        area.setCommander(commander);
        ULandscaper scaper = new ExampleDungeonScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        return area;
    }
}
