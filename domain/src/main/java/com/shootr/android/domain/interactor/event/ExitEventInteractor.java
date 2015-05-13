package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

public class ExitEventInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;

    private Callback callback;

    @Inject public ExitEventInteractor(final InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, SessionRepository sessionRepository, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
    }
    //endregion

    public void exitEvent(Callback callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        removeEventFromUser(currentUser);
        localUserRepository.putUser(currentUser);
        sessionRepository.setCurrentUser(currentUser);
        notifyLoaded();
        remoteUserRepository.putUser(currentUser);
        }

    private void removeEventFromUser(User currentUser) {
        currentUser.setIdWatchingEvent(null);
        currentUser.setWatchingEventTitle(null);
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
