package ure.dagger;

import dagger.Component;
import ure.URECommander;

import javax.inject.Singleton;

@Singleton
@Component(modules =  { AppModule.class })
public interface AppComponent {
    void inject(URECommander commander);
}
