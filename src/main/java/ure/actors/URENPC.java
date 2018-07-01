package ure.actors;

import java.util.Random;

public class URENPC extends UREActor {

    public int visionRange = 12;
    public int wakeRange = 20;

    Random random;

    @Override
    public void initialize() {
        super.initialize();
        random = new Random();
    }

    @Override
    public void hearTick() {
        Wander();
    }

    void Wander() {
        int dir = random.nextInt(4);
        if (dir == 0) {
            walkDir(-1,0);
        } else if (dir == 1) {
            walkDir(1, 0);
        } else if (dir == 2) {
            walkDir(0,1);
        } else {
            walkDir(0, -1);
        }
    }
}
