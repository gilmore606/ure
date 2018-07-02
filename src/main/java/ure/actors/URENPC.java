package ure.actors;

import java.util.Random;

public class URENPC extends UREActor {

    public int visionRange = 12;
    public int wakeRange = 20;
    public String[] ambients;

    Random random;

    @Override
    public void initialize() {
        super.initialize();
        random = new Random();
    }

    @Override
    public void hearTick() {
        float act = random.nextFloat();
        if (act < 0.5) {
            Wander();
        } else if (act < 0.7) {
            Ambient();
        }
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

    void Ambient() {
        if (ambients != null && ambients.length > 0) {
            area().commander().printScrollIfSeen(this, ambients[random.nextInt(ambients.length)]);
        }
    }
}
