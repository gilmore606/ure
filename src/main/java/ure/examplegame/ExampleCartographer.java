package ure.examplegame;

import ure.areas.UArea;
import ure.areas.UCartographer;

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
}
