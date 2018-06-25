package ure;

import java.lang.Math;

public class URECaveGenerator extends UREAreaGenerator {
    @Override
    public UREArea generate(URETerrainCzar terrainCzar, int totalSteps, int sizeX, int sizeY) {
        UREArea area = new UREArea(sizeX, sizeY, terrainCzar.getTerrainForFilechar('W'));

        int startingX = (int)Math.floor(sizeX/2);
        int startingY = (int)Math.floor(sizeY/2);
        int posX = startingX;
        int posY = startingY;

        for(int steps = 0; steps < totalSteps; steps++) {
            area.setTerrainOnCell(posX, posY, terrainCzar.getTerrainForFilechar('_'));

            int nextDir = this.random.nextInt(4);

            if(nextDir == 0) {
                posY--;
            } else if(nextDir == 1) {
                posY++;
            } else if(nextDir == 2) {
                posX--;
            } else if(nextDir == 3) {
                posX++;
            }

            if(!area.isValidXY(posX, posY)) {
                posX = startingX;
                posY = startingY;
            }
        }

        // debug
        area.setSeenEverything();

        return area;
    }
}