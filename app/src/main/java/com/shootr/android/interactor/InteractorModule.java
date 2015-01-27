package com.shootr.android.interactor;

import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.EventsCountInteractor;
import com.shootr.android.domain.interactor.EventsListInteractor;
import com.shootr.android.domain.interactor.EventsSearchInteractor;
import com.shootr.android.domain.interactor.GetPeopleInteractor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.SelectEventInteractor;
import com.shootr.android.domain.interactor.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.WatchingInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    JobInteractorHandler.class, GetPeopleInteractor.class, VisibleEventInfoInteractor.class, WatchingInteractor.class,
    EventsCountInteractor.class, EventsListInteractor.class, SelectEventInteractor.class, EventsSearchInteractor.class,
  },
  complete = false)
public class InteractorModule {

    @Provides @Singleton InteractorHandler provideInteractorHandler(JobInteractorHandler jobInteractorHandler) {
        return jobInteractorHandler;
    }

    @Provides @Singleton PostExecutionThread providePostExecutionThread() {
        return new UIThread();
    }
}
