package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import com.shootr.android.domain.utils.TimeUtils;
import javax.inject.Inject;

public class ExitEventInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final WatchRepository localWatchRepository;
    private final WatchRepository remoteWatchRepository;

    private Callback callback;

    @Inject public ExitEventInteractor(final InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local WatchRepository localWatchRepository,
      @Remote WatchRepository remoteWatchRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localWatchRepository = localWatchRepository;
        this.remoteWatchRepository = remoteWatchRepository;
    }
    //endregion

    public void exitEvent(Callback callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Watch oldVisibleEventWatch = localWatchRepository.getCurrentVisibleWatch();
        if (oldVisibleEventWatch != null) {
            oldVisibleEventWatch.setVisible(false);
            localWatchRepository.putWatch(oldVisibleEventWatch);
            notifyLoaded();
            remoteWatchRepository.putWatch(oldVisibleEventWatch);
        }

    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded();
            }
        });
    }

    public interface Callback {

        void onLoaded();

    }
}
