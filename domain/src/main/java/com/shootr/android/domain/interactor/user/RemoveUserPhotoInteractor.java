package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

public class RemoveUserPhotoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final SessionRepository sessionRepository;

    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public RemoveUserPhotoInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.sessionRepository = sessionRepository;
    }

    public void removeUserPhoto(CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        User user = getUserWithoutPhoto();
        updateLocalUser(user);
        updateRemoteUser(user);
    }

    private User getUserWithoutPhoto() {
        User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        user.setPhoto(null);
        return user;
    }

    private void updateLocalUser(User user) {
        localUserRepository.putUser(user);
        notifyLoaded();
    }

    private void updateRemoteUser(User user) {
        try {
            remoteUserRepository.putUser(user);
            notifyLoaded();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
