package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.UnwatchDone;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
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
      @com.shootr.mobile.domain.repository.Remote UserRepository remoteUserRepository, BusPublisher busPublisher) {
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
