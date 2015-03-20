package com.shootr.android.interactor;

import com.shootr.android.domain.dagger.ServiceModule;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.event.ChangeEventPhotoInteractor;
import com.shootr.android.domain.interactor.event.CreateEventInteractor;
import com.shootr.android.domain.interactor.event.EventsListInteractor;
import com.shootr.android.domain.interactor.event.EventsSearchInteractor;
import com.shootr.android.domain.interactor.event.GetEventInteractor;
import com.shootr.android.domain.interactor.shot.DeleteDraftInteractor;
import com.shootr.android.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.android.domain.interactor.shot.PostNewShotInteractor;
import com.shootr.android.domain.interactor.shot.SendDraftInteractor;
import com.shootr.android.domain.interactor.timeline.GetMainTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.RefreshMainTimelineInteractor;
import com.shootr.android.domain.interactor.user.GetPeopleInteractor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.event.WatchingInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  includes = { ServiceModule.class },
  injects = {
    GetPeopleInteractor.class, VisibleEventInfoInteractor.class, WatchingInteractor.class,
    EventsListInteractor.class, SelectEventInteractor.class,
    EventsSearchInteractor.class, CreateEventInteractor.class, GetEventInteractor.class,
    ChangeEventPhotoInteractor.class, PostNewShotInteractor.class, GetDraftsInteractor.class, SendDraftInteractor.class,
    DeleteDraftInteractor.class, GetMainTimelineInteractor.class, RefreshMainTimelineInteractor.class,
    InteractorModule.class
  },
  complete = false
)
public class InteractorModule {

    @Provides @Singleton InteractorHandler provideInteractorHandler(InteractorExecutor jobInteractorHandler) {
        return jobInteractorHandler;
    }

    @Provides @Singleton PostExecutionThread providePostExecutionThread() {
        return new UIThread();
    }
}
