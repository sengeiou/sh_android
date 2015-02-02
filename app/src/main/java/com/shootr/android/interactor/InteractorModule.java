package com.shootr.android.interactor;

import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.event.EventsWatchedCountInteractor;
import com.shootr.android.domain.interactor.event.EventsListInteractor;
import com.shootr.android.domain.interactor.event.EventsSearchInteractor;
import com.shootr.android.domain.interactor.user.GetPeopleInteractor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.event.WatchingInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    JobInteractorHandler.class, GetPeopleInteractor.class, VisibleEventInfoInteractor.class, WatchingInteractor.class,
    EventsWatchedCountInteractor.class, EventsListInteractor.class, SelectEventInteractor.class, EventsSearchInteractor.class,
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
