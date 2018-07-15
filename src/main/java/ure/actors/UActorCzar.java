package ure.actors;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.sys.Injector;

import javax.inject.Inject;

public class UActorCzar {

    private HashMap<String,UActor> actorsByName;

    @Inject
    ObjectMapper objectMapper;

    public UActorCzar() {
        Injector.getAppComponent().inject(this);
    }

    public void loadActors(String resourceName) {
        actorsByName = new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream(resourceName);
            UActor[] actorObjs = objectMapper.readValue(inputStream, UActor[].class);
            for (UActor actor: actorObjs) {
                actor.initialize();
                actorsByName.put(actor.getName(), actor);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public UActor getActorByName(String name) {
        return (UActor)actorsByName.get(name).getClone();
    }

    public String[] getActorsByTag(String tag, int level) {
        ArrayList<UActor> actors = new ArrayList<>();
        for (String actorname : actorsByName.keySet()) {
            UActor actor = actorsByName.get(actorname);
            if (actor.isTagAndLevel(tag, level)) {
                actors.add(actor);
            }
        }
        String[] names = new String[actors.size()];
        int i = 0;
        for (UActor actor: actors) {
            names[i] = actor.getName();
            i++;
        }
        return names;
    }
}
