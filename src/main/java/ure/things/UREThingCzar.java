package ure.things;

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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

public class UREThingCzar {

    private HashMap<String,UREThing> thingsByName;

    private Set<Class<? extends UREThing>> thingClasses;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Reflections reflections = new Reflections("", new SubTypesScanner());

    public UREThingCzar() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UREThing.class, new ThingDeserializer());
        objectMapper.registerModule(module);
    }

    public void loadThings(String resourceName) {
        thingClasses = reflections.getSubTypesOf(UREThing.class);
        thingsByName = new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/things.json");
            UREThing[] thingObjs = objectMapper.readValue(inputStream, UREThing[].class);
            for (UREThing thing: thingObjs) {
                thing.initialize();
                thingsByName.put(thing.name, thing);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public UREThing getThingByName(String name) {
        UREThing template = thingsByName.get(name);
        UREThing clone = null;
        return thingsByName.get(name).getClone();
    }

    public class ThingDeserializer extends JsonDeserializer<UREThing> {

        @Override
        public UREThing deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);
            JsonNode typeNode = node.get("type");
            String type = typeNode != null ? node.get("type").asText() : null;
            Class<? extends UREThing> thingClass = classForType(type);
            return objectMapper.treeToValue(node, thingClass);
        }

        private Class<? extends UREThing> classForType(String type) {
            if (type == null || type.equals("")) {
                return Blank.class;
            }
            try {
                for (Class<? extends UREThing> thingClass : thingClasses) {
                    Field typeField = thingClass.getField("TYPE");
                    String typeValue = (String) typeField.get(null);
                    if (type.equals(typeValue)) {
                        return thingClass;
                    }
                }
                throw new RuntimeException("No UREThing class of type '" + type + "' was found in the classpath");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("All sublcasses of UREThing must define a static TYPE field", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
