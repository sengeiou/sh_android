package com.shootr.android.interactor;

import com.shootr.android.domain.dagger.ServiceModule;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Fast;
import com.shootr.android.domain.interactor.InteractorHandler;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
