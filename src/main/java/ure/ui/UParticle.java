package ure.ui;

import ure.sys.UAnimator;
import ure.areas.UArea;

public class UParticle implements UAnimator {

    private int ticksLeft;

    private UArea area;
    private int x, y;

    public UParticle(UArea thearea, int thex, int they, int lifeticks) {
        area = thearea;
        x = thex;
        y = they;
        ticksLeft = lifeticks;
        area.addParticle(this);
    }

    public void animationTick() {
        ticksLeft--;
        if (ticksLeft < 0) {
            Fizzle();
        }
    }

    public void RedrawMyCell() {

    }

    void Fizzle() {
        System.out.println("fizzzz");
        area.fizzleParticle(this);
    }
}
