package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.User;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.UnwatchDone;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

public class UnwatchStreamInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final BusPublisher busPublisher;

    private CompletedCallback completedCallback;

    @Inject public UnwatchStreamInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, BusPublisher busPublisher) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.busPublisher = busPublisher;
    }

    public void unwatchStream(CompletedCallback completedCallback) {
        this.completedCallback = completedCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        User currentUser = getCurrentUser();
        User updatedUser = removeWatching(currentUser);
        putInLocal(updatedUser);
        notifyCompleted();
        putInRemote(updatedUser);
    }

    private void putInRemote(User updatedUser) {
        remoteUserRepository.updateWatch(updatedUser);
    }

    private void putInLocal(User updatedUser) {
        sessionRepository.setCurrentUser(updatedUser);
        localUserRepository.updateWatch(updatedUser);
    }

    private User getCurrentUser() {
        return localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    }

    protected User removeWatching(User currentUser) {
        currentUser.setIdWatchingStream(null);
        currentUser.setWatchingStreamTitle(null);
        currentUser.setJoinStreamDate(null);
        return currentUser;
    }

    private void notifyCompleted() {
        busPublisher.post(new UnwatchDone.Event());
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
