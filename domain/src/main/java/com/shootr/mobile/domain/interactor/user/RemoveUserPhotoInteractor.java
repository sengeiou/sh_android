package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.UserRepository;
import javax.inject.Inject;

public class RemoveUserPhotoInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;

    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public RemoveUserPhotoInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      @Local UserRepository localUserRepository, @com.shootr.mobile.domain.repository.Remote UserRepository remoteUserRepository,
      com.shootr.mobile.domain.repository.SessionRepository sessionRepository) {
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
        com.shootr.mobile.domain.User user = getUserWithoutPhoto();
        try {
            remoteUserRepository.putUser(user);
            localUserRepository.putUser(user);
            notifyLoaded();
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private com.shootr.mobile.domain.User getUserWithoutPhoto() {
        com.shootr.mobile.domain.User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
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

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
