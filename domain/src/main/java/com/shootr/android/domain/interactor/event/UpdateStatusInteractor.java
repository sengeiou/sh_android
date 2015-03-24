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

public class UpdateStatusInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final SessionRepository sessionRepository;

    private String userStatus;
    private Callback callback;

    @Inject public UpdateStatusInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void updateStatus(String userStatus, Callback callback) {
        this.userStatus = userStatus;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        setNewStatus();
    }

    private void setNewStatus() {
        User currentUser = sessionRepository.getCurrentUser();

        currentUser.setStatus(userStatus);

        sessionRepository.setCurrentUser(currentUser);
        localUserRepository.putUser(currentUser);
        notifyLoaded(currentUser);
        remoteUserRepository.putUser(currentUser);
    }

    private void notifyLoaded(final User watch) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(watch);
            }
        });
    }

    public interface Callback {

        void onLoaded(User currentUser);
    }
}
