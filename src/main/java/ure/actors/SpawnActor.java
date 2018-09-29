package ure.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.math.URandom;
import ure.sys.Injector;
import ure.sys.UCommander;

import javax.inject.Inject;

/**
 * SpawnActor represents a single actor to spawn, either a specific named actor, one of several, or selected from
 * given tags and levels.
 */
public class SpawnActor {

    @Inject
    @JsonIgnore
    URandom random;

    public String name;
    public float chance = 1f;
    public String[] oneof;

    public SpawnActor() { Injector.getAppComponent().inject(this); }

    public UActor spawn(UCommander commander) {
        // TODO: implement all of this
        return null;
    }

    public String getName() { return name; }
    public void setName(String s) { name = s; }
    public float getChance() { return chance; }
    public void setChance(float f) { chance = f; }
    public void setOneof(String[] s) { oneof = s; }
    public String[] getOneof() { return oneof; }

}
