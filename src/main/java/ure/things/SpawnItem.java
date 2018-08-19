package ure.things;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.math.URandom;
import ure.sys.Injector;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;

public class SpawnItem {

    @Inject
    @JsonIgnore
    URandom random;

    public String name;
    public float chance = 1f;
    public String[] oneof;

    public SpawnItem() {
        Injector.getAppComponent().inject(this);
    }

    public UThing spawn(UCommander commander) {
        if (random.f() > chance) return null;
        String tospawn = name;
        if (name == null) {
            if (oneof != null) {
                tospawn = (String)random.member(oneof);
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