import java.util.Iterator;

public interface UContainer {
    public static int TYPE_CELL = 1;
    public static int TYPE_THING = 2;

    public void addThing(UREThing thing);
    public void removeThing(UREThing thing);
    public Iterator<UREThing> iterator();
    public int containerType();
}
