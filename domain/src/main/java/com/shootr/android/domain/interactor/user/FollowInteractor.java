package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FollowRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class FollowInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    private String idUser;
    private CompletedCallback callback;

    @Inject public FollowInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository,
      @Remote FollowRepository remoteFollowRepository,
      @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void follow(String idUser, CompletedCallback callback) {
        this.idUser = checkNotNull(idUser);
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        localFollowRepository.follow(idUser);
        notifyCompleted();
        remoteFollowRepository.follow(idUser);

        ensureUserExistInLocal();
    }

    protected void ensureUserExistInLocal() {
        try {
            if (localUserRepository.getUserById(idUser) == null) {
                User user = remoteUserRepository.getUserById(idUser);
                localUserRepository.putUser(user);
            }
        } catch (ServerCommunicationException e) {
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
}
