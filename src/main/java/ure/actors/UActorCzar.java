package ure.actors;

import java.io.IOException;
import java.io.InputStream;
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

}
