package ure.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dagger.Module;
import dagger.Provides;
import ure.actors.ActorDeserializer;
import ure.actors.UActor;
import ure.terrain.TerrainDeserializer;
import ure.terrain.TerrainI;
import ure.things.ThingDeserializer;
import ure.things.ThingI;

import javax.inject.Singleton;

@Module
public class AppModule {

    @Provides
    @Singleton
    public ObjectMapper providesObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TerrainI.class, new TerrainDeserializer(objectMapper));
        module.addDeserializer(ThingI.class, new ThingDeserializer(objectMapper));
        module.addDeserializer(UActor.class, new ActorDeserializer(objectMapper));

        objectMapper.registerModule(module);
        return objectMapper;
    }

}
