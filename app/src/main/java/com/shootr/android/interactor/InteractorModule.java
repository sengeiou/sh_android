package com.shootr.android.interactor;

import com.shootr.android.domain.interactor.GetPeopleInteractor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.VisibleEventInfoInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    JobInteractorHandler.class, GetPeopleInteractor.class, VisibleEventInfoInteractor.class,
  },
  complete = false)
public class InteractorModule {

    @Provides @Singleton InteractorHandler provideInteractorHandler(JobInteractorHandler jobInteractorHandler) {
        return jobInteractorHandler;
    }
}