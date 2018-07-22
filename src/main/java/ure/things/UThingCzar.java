package ure.things;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.sys.Injector;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class UThingCzar {

    private HashMap<String,UThing> thingsByName;

    @Inject
    ObjectMapper objectMapper;
    @Inject
    UCommander commander;

    public UThingCzar() {
        Injector.getAppComponent().inject(this);
    }

    public void loadThings(String resourceName) {
        System.out.println("*** thingCzar loading from " + resourceName);
        thingsByName = new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/things.json");
            UThing[] thingObjs = objectMapper.readValue(inputStream, UThing[].class);
            for (UThing thing: thingObjs) {
                thing.initialize();
                thingsByName.put(thing.getName(), thing);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public UThing getThingByName(String name) {
        UThing clone = thingsByName.get(name).makeClone();
        clone.initialize();
        if (clone.getContents() == null) {
            System.out.println("*** BUG thingCzar spawned a clone with null contents");
        }
        if (clone.getContents().getThings() == null) {
            System.out.println("+++ BUG thingCzar spawned a clone with contents with null things");
            if (clone.closed) {
                System.out.println("IT WAS A CLOSED THING");
                if (thingsByName.get(name).closed) {
                    System.out.println("THE SOURCE WAS CLOSED TOO");
                }
            }
        }
        clone.setID(commander.generateNewID(clone));
        return clone;
    }

    public String[] getThingsByTag(String tag, int level) {
        ArrayList<UThing> things = new ArrayList<>();
        for (String actorname : thingsByName.keySet()) {
            UThing thing = thingsByName.get(actorname);
            if (thing.isTagAndLevel(tag, level)) {
                things.add(thing);
            }
        }
        String[] names = new String[things.size()];
        int i = 0;
        for (UThing thing: things) {
            names[i] = thing.getName();
            i++;
        }
        return names;
    }
}
