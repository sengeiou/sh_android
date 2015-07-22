package com.shootr.android.interactor;

import com.shootr.android.domain.dagger.ServiceModule;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.event.ChangeStreamPhotoInteractor;
import com.shootr.android.domain.interactor.event.CreateStreamInteractor;
import com.shootr.android.domain.interactor.event.StreamsListInteractor;
import com.shootr.android.domain.interactor.event.StreamSearchInteractor;
import com.shootr.android.domain.interactor.event.GetStreamInteractor;
import com.shootr.android.domain.interactor.event.SelectStreamInteractor;
import com.shootr.android.domain.interactor.event.VisibleStreamInfoInteractor;
import com.shootr.android.domain.interactor.shot.DeleteDraftInteractor;
import com.shootr.android.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.android.domain.interactor.shot.PostNewShotInteractor;
import com.shootr.android.domain.interactor.shot.SendDraftInteractor;
import com.shootr.android.domain.interactor.user.GetPeopleInteractor;
import com.shootr.android.domain.interactor.user.SendPasswordResetEmailInteractor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  includes = { ServiceModule.class },
  injects = {
    GetPeopleInteractor.class, VisibleStreamInfoInteractor.class,
    StreamsListInteractor.class, SelectStreamInteractor.class,
    StreamSearchInteractor.class, CreateStreamInteractor.class, GetStreamInteractor.class,
    ChangeStreamPhotoInteractor.class, PostNewShotInteractor.class, GetDraftsInteractor.class, SendDraftInteractor.class,
    DeleteDraftInteractor.class,
    InteractorModule.class, SendPasswordResetEmailInteractor.class,
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
