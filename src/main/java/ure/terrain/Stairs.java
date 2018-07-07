package ure.terrain;

import ure.UArea;
import ure.UCartographer;
import ure.actors.UActor;

public class Stairs extends TerrainI implements UTerrain {

    public static final String TYPE = "stairs";

    String label = "";

    int destX, destY;
    boolean onstep = false;
    boolean confirm = true;

    public String label() {
        return label;
    }

    public void setLabel(String thelabel) {
        label = thelabel;
        System.out.println("CARTO : setting stairs dest : " + label);
    }

    public void transportActor(UActor actor, UCartographer carto) {
        // TODO: cool transition bullshit i dunno
        UArea destarea = carto.getArea(label);
        actor.moveToCell(destarea, 20, 20);
    }
}
