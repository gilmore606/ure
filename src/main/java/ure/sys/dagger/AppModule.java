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
import ure.render.URenderer;
import ure.render.URendererOGL;
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
import ure.ui.Icons.Icon;
import ure.ui.Icons.IconDeserializer;
import ure.ui.Icons.UIconCzar;
import ure.ui.USpeaker;

import javax.inject.Singleton;
import java.util.Random;

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
        module.addDeserializer(Icon.class, new IconDeserializer(objectMapper));
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
    public URenderer providesRenderer() {
        URenderer rend = new URendererOGL();
        rend.initialize();
        return rend;
    }

    @Provides
    @Singleton
    public UActorCzar providesActorCzar() {
        UActorCzar czar = new UActorCzar();
        //czar.loadActors("/actors.json"); -- can't do this here because it relies on some other things being loaded
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
    public UIconCzar providesIconCzar() {
        UIconCzar czar = new UIconCzar();
        czar.loadIcons();
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

    @Provides
    @Singleton
    public Random providesRandom() {
        return new Random();
    }

    @Provides
    @Singleton
    public USpeaker providesSpeaker() {
        USpeaker s = new USpeaker();
        s.initialize();
        return s;
    }
}
