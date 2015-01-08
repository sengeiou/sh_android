package com.shootr.android.interactor;

import com.shootr.android.domain.interactor.GetPeopleInteractor;
import com.shootr.android.domain.interactor.InteractorHandler;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    JobInteractorHandler.class, GetPeopleInteractor.class,
  },
  complete = false)
public class InteractorModule {

    @Provides @Singleton InteractorHandler provideInteractorHandler(JobInteractorHandler jobInteractorHandler) {
        return jobInteractorHandler;
    }
}
