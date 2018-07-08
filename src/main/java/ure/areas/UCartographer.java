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

    public UArea getArea(String label) {
        String labelname = GetLabelName(label);
        int[] labeldata = GetLabelData(label);

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
        area.setLabel(label);
        area.setCommander(commander);
        commander.registerTimeListener(area);
        return area;
    }

    public String GetLabelName(String label) {
        int i = label.indexOf(" ");
        if (i < 1) return label;
        return label.substring(0,i);
    }
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

    public UArea MakeForest() {
        UArea area = new UArea(140, 140, terrainCzar, "grass");
        area.setCommander(commander);
        ULandscaper scaper = new ExampleForestScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area);
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
        scaper.SetStairsLabels(area);
        return area;
    }

    public UArea MakeCavern(int depth) {
        UArea area = new UArea(70, 70, terrainCzar, "wall");
        area.setCommander(commander);
        ULandscaper scaper = new ExampleCaveScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area);
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
        return area;
    }
}
