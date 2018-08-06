package ure.sys.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.eventbus.EventBus;
import dagger.Module;
import dagger.Provides;
import ure.areas.LandscaperDeserializer;
import ure.areas.UCartographer;
import ure.areas.ULandscaper;
import ure.actors.behaviors.BehaviorDeserializer;
import ure.actors.behaviors.UBehavior;
import ure.sys.UCommander;
import ure.actors.ActorDeserializer;
import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.sys.UConfig;
import ure.terrain.TerrainDeserializer;
import ure.terrain.UTerrain;
import ure.terrain.UTerrainCzar;
import ure.things.ThingDeserializer;
import ure.things.UThing;
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
        module.addDeserializer(UTerrain.class, new TerrainDeserializer(objectMapper));
        module.addDeserializer(UThing.class, new ThingDeserializer(objectMapper));
        module.addDeserializer(UActor.class, new ActorDeserializer(objectMapper));
        module.addDeserializer(ULandscaper.class, new LandscaperDeserializer(objectMapper));
        module.addDeserializer(UBehavior.class, new BehaviorDeserializer(objectMapper));
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
        //czar.loadActors("/actors.json");
        return czar;
    }

    @Provides
    @Singleton
    public UTerrainCzar providesTerrainCzar() {
        UTerrainCzar czar = new UTerrainCzar();
        czar.loadTerrains();
        return czar;
    }

    @Provides
    @Singleton
    public UThingCzar providesThingCzar() {
        UThingCzar czar = new UThingCzar();
        czar.loadThings();
        return czar;
    }

    @Provides
    @Singleton
    public UCartographer providesCartographer() {
        UCartographer cartographer = new UCartographer();
        return cartographer;
    }

    @Provides
    @Singleton
    public EventBus providesEventBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    public UConfig providesConfig() {
        return new UConfig();
    }
}
