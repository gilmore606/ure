package ure.dagger;

import dagger.Component;
import ure.actors.UActorCzar;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;

import javax.inject.Singleton;

@Singleton
@Component(modules =  { AppModule.class })
public interface AppComponent {
    void inject(UTerrainCzar czar);
    void inject(UThingCzar czar);
    void inject(UActorCzar czar);
}
