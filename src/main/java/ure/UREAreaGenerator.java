package ure;

import java.util.Random;

public abstract class UREAreaGenerator {
    protected static Random random = new Random();

    public abstract UREArea generate(URETerrainCzar terrainCzar, int totalSteps, int sizeX, int sizeY);
}
