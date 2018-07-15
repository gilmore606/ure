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
                area = MakeForest(label); break;
            case "cavern":
                area = MakeCavern(label, labeldata[0]); break;
            case "dungeon":
                area = MakeDungeon(label); break;
            case "complex":
                area = MakeComplex(label); break;
            default:
                area = MakeForest(label); break;

        }
        return area;
    }

    public UArea MakeForest(String label) {
        UArea area = new UArea(140, 140, terrainCzar, "grass");
        area.setLabel(label);
        ULandscaper scaper = new ExampleForestScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        return area;
    }

    public UArea MakeComplex(String label) {
        UArea area = new UArea(70, 70, terrainCzar, "grass");
        area.setLabel(label);
        ULandscaper scaper = new ExampleComplexScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        return area;
    }

    public UArea MakeCavern(String label, int depth) {
        UArea area = new UArea(70, 70, terrainCzar, "wall");
        area.setLabel(label);
        ULandscaper scaper = new ExampleCaveScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        return area;
    }

    public UArea MakeDungeon(String label) {
        UArea area = new UArea(101, 101, terrainCzar, "wall");
        area.setLabel(label);
        ULandscaper scaper = new ExampleDungeonScaper(terrainCzar, thingCzar);
        scaper.buildArea(area);
        scaper.SetStairsLabels(area, this);
        return area;
    }
}
