package ure;

import java.awt.*;

public class UREActor  extends UREThing implements UAnimator {

    URECamera camera;
    private int animationFrame = 0, animationFrames = 4;

    public UREActor(String thename, char theicon, UColor thecolor, boolean addOutline) {
        super(thename, theicon, thecolor, addOutline);
    }

    public void attachCamera(URECamera thecamera) {
        camera = thecamera;
        camera.addVisibilitySource(this);
    }
    public void detachCamera() {
        camera.removeVisibilitySource(this);
        camera = null;
    }

    public void walkDir(int xdir, int ydir) {
        int destX = xdir + areaX();
        int destY = ydir + areaY();
        if (location.containerType() == UContainer.TYPE_CELL) {
            if (area().willAcceptThing(this, destX, destY)) {
                moveToCell(area(), destX, destY);
            }
        }
    }

    @Override
    public void moveToCell(UREArea thearea, int destX, int destY) {
        if (camera != null) {
            camera.moveTo(area(), destX, destY);
        }
        super.moveToCell(thearea, destX, destY);
    }

    public void debug() {
        URELight newlight = new URELight(new UColor(Color.WHITE), 10);
        newlight.moveTo(area(), areaX(), areaY());
        float sun = area().sunBrightnessAt(areaX(), areaY());
        System.out.println("sun " + Float.toString(sun));
        area().commander().printScroll("hello from " + Integer.toString(areaX()) + "," + Integer.toString(areaY()));
  }

    public void animationTick() {
        animationFrame++;
        if (animationFrame >= animationFrames)
            animationFrame = 0;
        if (icon == '@')
            icon = 'o';
        else
            icon = '@';
        RedrawMyCell();
    }

    public void RedrawMyCell() {
        area().redrawCell(areaX(),areaY());
    }
}
