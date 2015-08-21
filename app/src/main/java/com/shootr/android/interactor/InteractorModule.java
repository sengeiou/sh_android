package com.shootr.android.interactor;

import com.shootr.android.domain.dagger.ServiceModule;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Fast;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.shot.DeleteDraftInteractor;
import com.shootr.android.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.android.domain.interactor.shot.PostNewShotInteractor;
import com.shootr.android.domain.interactor.shot.SendDraftInteractor;
import com.shootr.android.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.android.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.android.domain.interactor.stream.GetStreamInteractor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.domain.interactor.stream.StreamSearchInteractor;
import com.shootr.android.domain.interactor.stream.StreamsListInteractor;
import com.shootr.android.domain.interactor.stream.VisibleStreamInfoInteractor;
import com.shootr.android.domain.interactor.user.GetPeopleInteractor;
import com.shootr.android.domain.interactor.user.SendPasswordResetEmailInteractor;
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
