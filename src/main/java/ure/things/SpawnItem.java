package ure.things;

import ure.sys.UCommander;

public class SpawnItem {
    public String name;
    public float chance = 1f;
    public String[] oneof;

    public SpawnItem() { }

    public UThing spawn(UCommander commander) {
        if (commander.random.nextFloat() > chance) return null;
        String tospawn = name;
        if (name == null) {
            if (oneof != null) {
                tospawn = oneof[commander.random.nextInt(oneof.length)];
            }
        }
        if (tospawn != null) {
            return commander.thingCzar.getThingByName(tospawn);
        }
        return null;
    }

    public String getName() { return name; }
    public void setName(String s) { name = s; }
    public float getChance() { return chance; }
    public void setChance(float f) { chance = f; }
    public void setOneof(String[] s) { oneof = s; }
    public String[] getOneof() { return oneof; }

}