/**
 * Created by gilmore on 6/20/2018.
 *
 * A bunch of Things in a place.
 *
 */

import java.util.HashSet;
import java.util.Iterator;

public class UCollection {

    private UContainer container;
    private HashSet<UREThing> things;

    public UCollection(UContainer cont) {
        container = cont;
        things = new HashSet<UREThing>();
    }

    public void remove(UREThing thing) {
        things.remove(thing);
    }

    public void add(UREThing thing) {
        things.add(thing);
    }

    public Iterator<UREThing> iterator() {
        return things.iterator();
    }
}
