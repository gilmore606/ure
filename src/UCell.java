/**
 * Created by gilmore on 6/20/2018.
 *
 * A single xy grid cell of an Area
 *
 */
public class UCell implements UContainer {
    UREArea area;
    URETerrain terrain;
    UCollection contents;
    boolean isSeen = false;

    public UCell(UREArea theArea, URETerrain theTerrain) {
        contents = new UCollection(this);
        area = theArea;
        setTerrain(theTerrain);
    }

    public void setTerrain(URETerrain theTerrain) {
        terrain = theTerrain;
    }

    public URETerrain getTerrain() {
        return terrain;
    }

    public void addThing(UREThing thing) {
        contents.add(thing);
    }
    public void removeThing(UREThing thing) {
        contents.remove(thing);
        area.hearRemoveThing(thing);
    }
}
