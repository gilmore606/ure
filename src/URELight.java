/**
 * An instance of a light source somewhere in an area
 * Not a thing, but a thing can create one
 */
import java.lang.Math;

public class URELight {
    public int[] color;
    public int range;

    UREArea area;
    public int x,y;

    public URELight(int[] thecolor, int therange) {
        color = thecolor;
        range = therange;
    }

    public void close() {
        removeFromArea();
    }

    public void moveTo(UREArea thearea, int thex, int they) {
        x = thex;
        y = they;
        if (area != thearea) {
            if (area != null) {
                area.removeLight(this);
            }
            area = thearea;
            area.addLight(this);
        }
    }

    public void removeFromArea() {
        if (area != null) {
            area.removeLight(this);
        }
        area = null;
    }

    public boolean canTouch(URECamera camera) {
        int circleDistX = Math.abs(x - camera.centerX);
        int circleDistY = Math.abs(y - camera.centerY);
        if (circleDistX > (camera.width/2 + range)) return false;
        if (circleDistY > (camera.height/2 + range)) return false;
        if (circleDistX <= (camera.width/2)) return true;
        if (circleDistY <= (camera.height/2)) return true;
        double cornerDistSq = Math.pow(circleDistX - camera.width/2, 2) + Math.pow(circleDistY - camera.height/2, 2);
        if (cornerDistSq <= Math.pow(range,2)) return true;
        return false;
    }

    public void renderInto(URECamera camera) {

    }
}
