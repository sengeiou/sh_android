package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class UnfollowInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;

    private String idUser;
    private CompletedCallback callback;

    @Inject public UnfollowInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository,
      @com.shootr.mobile.domain.repository.Remote FollowRepository remoteFollowRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
    }

    public void unfollow(String idUser, CompletedCallback callback) {
        this.idUser = checkNotNull(idUser);
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        localFollowRepository.unfollow(idUser);
        notifyCompleted();
        remoteFollowRepository.unfollow(idUser);
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
