import java.util.Iterator;

public interface UContainer {
    public void addThing(UREThing thing);
    public void removeThing(UREThing thing);
    public Iterator<UREThing> iterator();
}
