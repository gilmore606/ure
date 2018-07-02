package ure.actors;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class UREActorCzar {

    private HashMap<String,UREActor> actorsByName;

    private Set<Class<? extends UREActor>> actorClasses;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Reflections reflections = new Reflections("ure", new SubTypesScanner());

    public UREActorCzar() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UREActor.class, new ActorDeserializer());
        objectMapper.registerModule(module);
    }

    public void loadActors(String resourceName) {
        actorClasses = reflections.getSubTypesOf(UREActor.class);
        actorsByName = new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/actors.json");
            UREActor[] actorObjs = objectMapper.readValue(inputStream, UREActor[].class);
            for (UREActor actor: actorObjs) {
                actor.initialize();
                actorsByName.put(actor.name, actor);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public UREActor getActorByName(String name) {
        return (UREActor)actorsByName.get(name).getClone();
    }

    public class ActorDeserializer extends JsonDeserializer<UREActor> {

        @Override
        public UREActor deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);
            JsonNode typeNode = node.get("type");
            String type = typeNode != null ? node.get("type").asText() : null;
            Class<? extends UREActor> actorClass = classForType(type);
            return objectMapper.treeToValue(node, actorClass);
        }

        private Class<? extends UREActor> classForType(String type) {
            if (type == null || type.equals("")) {
                return Blank.class;
            }
            try {
                for (Class<? extends UREActor> actorClass : actorClasses) {
                    Field typeField = actorClass.getField("TYPE");
                    String typeValue = (String) typeField.get(null);
                    if (type.equals(typeValue)) {
                        return actorClass;
                    }
                }
                throw new RuntimeException("No UREActor class of type '" + type + "' was found in the classpath");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("All subclasses of UREActor must define a static TYPE field", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
