package ure.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.sys.UAnimator;
import ure.areas.UArea;

public class UParticle implements UAnimator {

    private int ticksLeft;

    @JsonIgnore
    private UArea area;  // TODO: Reconnect after deserialization

    private int x, y;

    public UParticle(UArea thearea, int thex, int they, int lifeticks) {
        area = thearea;
        x = thex;
        y = they;
        ticksLeft = lifeticks;
        area.addParticle(this);
    }

    public void reconnect(UArea area) {
        this.area = area;
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
