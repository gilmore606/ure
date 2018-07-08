package ure.dagger;

import dagger.Module;
import dagger.Provides;
import ure.URECommander;
import ure.actors.UREActorCzar;
import ure.render.URERenderer;
import ure.render.URERendererOGL;
import ure.terrain.URETerrainCzar;
import ure.things.UREThingCzar;

import javax.inject.Singleton;

@Module
public class AppModule {

    @Provides
    @Singleton
    public URERenderer providesRenderer() {
        return new URERendererOGL();
    }

    @Provides
    @Singleton
    public UREActorCzar providesActorCzar() {
        return new UREActorCzar();
    }

    @Provides
    @Singleton
    public URETerrainCzar providesTerrainCzar() {
        return new URETerrainCzar();
    }

    @Provides
    @Singleton
    public UREThingCzar providesThingCzar() {
        return new UREThingCzar();
    }

//    @Provides
//    @Singleton
//    public URECommander provideCommander(URERenderer renderer, URETerrainCzar terrainCzar, UREThingCzar thingCzar) {
//        return new URECommander();
//    }
}
