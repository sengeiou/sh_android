package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class GetBannedUsersInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;

    private Callback<List<String>> callback;
    private ErrorCallback errorCallback;

    @Inject
    public GetBannedUsersInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository, @Remote FollowRepository remoteFollowRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
    }

    public void loadBannedIdUsers(Callback<List<String>> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        tryLoadingLocalUsersAndThenRemote();
    }

    private void tryLoadingLocalUsersAndThenRemote() {
        List<String> blockedIdUsers = localFollowRepository.getBannedIdUsers();
        if (blockedIdUsers == null) {
            loadRemoteBannedIdUsers();
        } else {
            notifyResult(blockedIdUsers);
        }
    }

    private void loadRemoteBannedIdUsers() {
        try {
            notifyResult(remoteFollowRepository.getBannedIdUsers());
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void notifyResult(final List<String> user) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(user);
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