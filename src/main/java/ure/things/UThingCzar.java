package ure.things;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.sys.Injector;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class UThingCzar {

    private HashMap<String,ThingI> thingsByName;

    @Inject
    ObjectMapper objectMapper;

    public UThingCzar() {
        Injector.getAppComponent().inject(this);
    }

    public void loadThings(String resourceName) {
        thingsByName = new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/things.json");
            ThingI[] thingObjs = objectMapper.readValue(inputStream, ThingI[].class);
            for (ThingI thing: thingObjs) {
                thing.initialize();
                thingsByName.put(thing.getName(), thing);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public UThing getThingByName(String name) {
        return (UThing)(thingsByName.get(name).getClone());
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
