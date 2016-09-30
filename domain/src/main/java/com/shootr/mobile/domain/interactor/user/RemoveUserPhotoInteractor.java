package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
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
        try {
            remoteUserRepository.putUser(user);
            localUserRepository.putUser(user);
            notifyLoaded();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private User getUserWithoutPhoto() {
        User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        user.setPhoto(null);
        return user;
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
