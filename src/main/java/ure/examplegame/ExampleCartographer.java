package ure.examplegame;

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
}
