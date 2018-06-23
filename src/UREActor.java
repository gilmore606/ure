import java.awt.*;

public class UREActor  extends UREThing {

    public UREActor(String thename, char theicon, Color thecolor, boolean addOutline) {
        super(thename, theicon, thecolor, addOutline);
    }

    public void walkDir(int xdir, int ydir) {
        if (location != null)
            if (location.containerType() == UContainer.TYPE_CELL) {

            }
    }
}
