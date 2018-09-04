package ure.things;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.sys.Injector;
import ure.sys.ResourceManager;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class UThingCzar {

    private HashMap<String,UThing> thingsByName;

    @Inject
    ObjectMapper objectMapper;
    @Inject
    UCommander commander;
    @Inject
    ResourceManager resourceManager;

    private Log log = LogFactory.getLog(UThingCzar.class);

    public UThingCzar() {
        Injector.getAppComponent().inject(this);
    }

    public void loadThings() {
        thingsByName = new HashMap<>();
        for (String resource : resourceManager.getResourceFiles("/things")) {
            if (resource.endsWith(".json")) {
                try {
                    InputStream inputStream = getClass().getResourceAsStream("/things/" + resource);
                    UThing[] thingObjs = objectMapper.readValue(inputStream, UThing[].class);
                    for (UThing thing : thingObjs) {
                        thing.initializeAsTemplate();
                        thingsByName.put(thing.name, thing);
                        log.debug("loaded " + thing.getName());
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    public UThing getThingByName(String name) {
        UThing template = thingsByName.get(name);
        UThing clone = template.makeClone();
        clone.initializeAsCloneFrom(template);
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

    public UThing getPile(String name, int count) {
        UThing pile = getThingByName(name);
        if (pile != null) {
            ((Pile)pile).setCount(count);
        }
        return pile;
    }

    public Set<String> getAllThings() { return thingsByName.keySet(); }
}
