package ure.actors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.behaviors.BehaviorDeserializer;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.ui.Icons.UIconCzar;

import javax.inject.Inject;

public class UActorCzar {

    private HashMap<String,UActor> actorsByName;
    private HashMap<String,Body> bodies;

    @Inject
    ObjectMapper objectMapper;
    @Inject
    UCommander commander;
    @Inject
    UIconCzar iconCzar;

    public BehaviorDeserializer behaviorDeserializer;

    private Log log = LogFactory.getLog(UActorCzar.class);

    public UActorCzar() {
        Injector.getAppComponent().inject(this);
    }

    public void loadActors() {
        bodies = new HashMap<>();
        File jsonDir = new File(commander.config.getResourcePath() + "bodies/");
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(jsonDir.listFiles()));
        for (File resourceFile : files) {
            String resourceName = resourceFile.getName();
            if (resourceName.endsWith(".json")) {
                log.debug("loading " + resourceName);
                try {
                    InputStream inputStream = getClass().getResourceAsStream("/bodies/" + resourceName);
                    Body[] bodyObjs = objectMapper.readValue(inputStream, Body[].class);
                    for (Body body : bodyObjs) {
                        bodies.put(body.getName(), body);
                        log.info("loaded " + body.getName());
                    }
                } catch (IOException io) {
                    throw new RuntimeException("Failed to load " + resourceName, io);
                }
            }
        }

        behaviorDeserializer = new BehaviorDeserializer(objectMapper);
        actorsByName = new HashMap<>();
        jsonDir = new File(commander.config.getResourcePath() + "actors/");
        files = new ArrayList<File>(Arrays.asList(jsonDir.listFiles()));
        for (File resourceFile : files) {
            String resourceName = resourceFile.getName();
            if (resourceName.endsWith(".json")) {
                log.debug("loading " + resourceName);
                try {
                    InputStream inputStream = getClass().getResourceAsStream("/actors/" + resourceName);
                    UActor[] actorObjs = objectMapper.readValue(inputStream, UActor[].class);
                    for (UActor actor : actorObjs) {
                        actor.initializeAsTemplate();
                        actorsByName.put(actor.getName(), actor);
                        log.info("loaded " + actor.getName());
                    }
                } catch (IOException io) {
                    throw new RuntimeException("Failed to load " + resourceName, io);
                }
            }
        }


    }

    public UActor getActorByName(String name) {
        UActor template = (UActor)actorsByName.get(name);
        UActor clone = (UActor)template.makeClone();
        clone.initializeAsCloneFrom(template);
        clone.setID(commander.generateNewID(clone));
        return clone;
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

    public Body getNewBody(String bodytype) {
        Body template = bodies.get(bodytype);
        if (template == null)
            return null;
        Body body = new Body();
        body.setName(template.getName());
        ArrayList<Bodypart> parts = new ArrayList<>();
        for (Bodypart part : template.getParts()) {
            parts.add(new Bodypart(part.getName(), part.getSlots(), part.getSize()));
        }
        body.setParts(parts);
        return body;
    }

    public Set<String> getAllActors() { return actorsByName.keySet(); }
}
