package ure.examplegame;

import ure.actors.UActor;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.areas.UCell;
import ure.areas.ULandscaper;

public class ExampleCartographer extends UCartographer {

    @Override
    public String describeLabel(String label) {
        String labelname = GetLabelName(label);
        int[] labeldata = GetLabelData(label);
        switch (labelname) {
            case "forest":
                return "Mystic forest";
            case "cavern":
                return "Cavern, " + Integer.toString(labeldata[0] * 25) + "ft";
            default:
                return "plane of chaos";
        }
    }

    @Override
    public UArea makeArea(String label, String labelname, int[] labeldata) {
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

    public UArea MakeForest() {
        UArea area = new UArea(140, 140, terrainCzar, "grass");
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
        ULandscaper scaper = new ExampleComplexScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        return area;
    }

    public UArea MakeCavern(int depth) {
        UArea area = new UArea(70, 70, terrainCzar, "wall");
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
        ULandscaper scaper = new ExampleDungeonScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        return area;
    }
}
