package ure.actors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

public class ActorDeserializer extends JsonDeserializer<UActor> {

    private ObjectMapper objectMapper;

    private Reflections reflections = new Reflections("ure", new SubTypesScanner());
    private Set<Class<? extends UActor>> actorClasses = reflections.getSubTypesOf(UActor.class);

    public ActorDeserializer(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    @Override
    public UActor deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        JsonNode typeNode = node.get("type");
        String type = typeNode != null ? node.get("type").asText() : null;
        Class<? extends UActor> actorClass = classForType(type);
        return objectMapper.treeToValue(node, actorClass);
    }

    private Class<? extends UActor> classForType(String type) {
        if (type == null || type.equals("")) {
            return Blank.class;
        }
        try {
            for (Class<? extends UActor> actorClass : actorClasses) {
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
