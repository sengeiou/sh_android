package com.shootr.mobile.interactor;

import com.shootr.mobile.domain.dagger.ServiceModule;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Fast;
import com.shootr.mobile.domain.interactor.InteractorHandler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
  includes = { ServiceModule.class },
  injects = {
  },
  complete = false,
  library = true
)
public class InteractorModule {

    @Provides @Singleton InteractorHandler provideInteractorHandler(InteractorExecutor jobInteractorHandler) {
        return jobInteractorHandler;
    }

    @Provides
    @Fast
    InteractorHandler provideFastInteractorHandler(MainThreadInteractorExecutor mainThreadInteractorExecutor) {
        return mainThreadInteractorExecutor;
    }

    @Provides @Singleton PostExecutionThread providePostExecutionThread() {
        return new UIThread();
    }
}
