package ure.things;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.sys.Injector;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
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
                thingsByName.put(thing.name, thing);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public UThing getThingByName(String name) {
        return (UThing)(thingsByName.get(name).getClone());
    }

}
