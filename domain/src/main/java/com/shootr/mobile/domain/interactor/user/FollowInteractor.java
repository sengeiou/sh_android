package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.UserRepository;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class FollowInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    private String idUser;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject public FollowInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository,
      @com.shootr.mobile.domain.repository.Remote FollowRepository remoteFollowRepository,
      @com.shootr.mobile.domain.repository.Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void follow(String idUser, CompletedCallback callback, ErrorCallback errorCallback) {
        this.idUser = checkNotNull(idUser);
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        try {
            remoteFollowRepository.follow(idUser);
            localFollowRepository.follow(idUser);
            notifyCompleted();
            ensureUserExistInLocal();
        } catch (com.shootr.mobile.domain.exception.FollowingBlockedUserException error) {
            notifyError(new com.shootr.mobile.domain.service.user.CannotFollowBlockedUserException(error));
        }
    }

    protected void ensureUserExistInLocal() {
        try {
            if (localUserRepository.getUserById(idUser) == null) {
                User user = remoteUserRepository.getUserById(idUser);
                localUserRepository.putUser(user);
            }
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException e) {
            /* bad luck: will have unconsistent data for a short period of time */
        }
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onCompleted();
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
