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

public class UThingCzar {

    private HashMap<String,ThingI> thingsByName;

    private Set<Class<? extends ThingI>> thingClasses;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Reflections reflections = new Reflections("ure", new SubTypesScanner());

    public UThingCzar(String jsonfilename) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ThingI.class, new ThingDeserializer());
        objectMapper.registerModule(module);
        loadThings(jsonfilename);
    }

    public void loadThings(String resourceName) {
        thingClasses = reflections.getSubTypesOf(ThingI.class);
        System.out.println("++++++++ " + thingClasses.size() + " ThingI subclasses");
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

    public class ThingDeserializer extends JsonDeserializer<ThingI> {

        @Override
        public ThingI deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);
            JsonNode typeNode = node.get("type");
            String type = typeNode != null ? node.get("type").asText() : null;
            Class<? extends ThingI> thingClass = classForType(type);
            return objectMapper.treeToValue(node, thingClass);
        }

        private Class<? extends ThingI> classForType(String type) {
            if (type == null || type.equals("")) {
                return Blank.class;
            }
            try {
                for (Class<? extends ThingI> thingClass : thingClasses) {
                    Field typeField = thingClass.getField("TYPE");
                    String typeValue = (String) typeField.get(null);
                    if (type.equals(typeValue)) {
                        return thingClass;
                    }
                }
                throw new RuntimeException("No ThingI class of type '" + type + "' was found in the classpath");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("All sublcasses of ThingI must define a static TYPE field", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
