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
        if (act < 0.8f)
            HuntPlayer();
    }

    void HuntPlayer() {
        System.out.println("hunt from " + Integer.toString(areaX()) + "," + Integer.toString(areaY()));
        int[] step = path.nextStep(area(), areaX(), areaY(), area().commander().player().areaX(), area().commander().player().areaY(), this, 25);
        if (step != null) {
            walkDir(step[0] - areaX(), step[1] - areaY());
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
