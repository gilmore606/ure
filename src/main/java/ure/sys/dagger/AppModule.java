package ure.sys.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dagger.Module;
import dagger.Provides;
import ure.sys.UCommander;
import ure.actors.ActorDeserializer;
import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.terrain.TerrainDeserializer;
import ure.terrain.TerrainI;
import ure.terrain.UTerrainCzar;
import ure.things.ThingDeserializer;
import ure.things.ThingI;
import ure.things.UThingCzar;

import javax.inject.Singleton;

/**
 *
 * The register of providers for dependency injection.
 *
 */
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

    @Provides
    @Singleton
    public UCommander providesCommander() {
        UCommander cmdr = new UCommander();
        return cmdr;
    }

    @Provides
    @Singleton
    public UActorCzar providesActorCzar() {
        UActorCzar czar = new UActorCzar();
        czar.loadActors("/actors.json");
        return czar;
    }

    @Provides
    @Singleton
    public UTerrainCzar providesTerrainCzar() {
        UTerrainCzar czar = new UTerrainCzar();
        czar.loadTerrains("/terrain.json");
        return czar;
    }

    @Provides
    @Singleton
    public UThingCzar providesThingCzar() {
        UThingCzar czar = new UThingCzar();
        czar.loadThings("/things.json");
        return czar;
    }
}
