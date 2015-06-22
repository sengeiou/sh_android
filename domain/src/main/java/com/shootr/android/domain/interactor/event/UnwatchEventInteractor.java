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

public class UnwatchEventInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;

    private CompletedCallback completedCallback;

    @Inject public UnwatchEventInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void unwatchEvent(CompletedCallback completedCallback) {
        this.completedCallback = completedCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        User currentUser = getCurrentUser();
        User updatedUser = removeWatching(currentUser);
        putInLocal(updatedUser);
        notifyCompleted();
        putInRemote(updatedUser);
    }

    private void putInRemote(User updatedUser) {
        remoteUserRepository.putUser(updatedUser);
    }

    private void putInLocal(User updatedUser) {
        sessionRepository.setCurrentUser(updatedUser);
        localUserRepository.putUser(updatedUser);
    }

    private User getCurrentUser() {
        return localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    }

    protected User removeWatching(User currentUser) {
        currentUser.setIdWatchingEvent(null);
        currentUser.setWatchingEventTitle(null);
        currentUser.setJoinEventDate(null);
        return currentUser;
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }
}
