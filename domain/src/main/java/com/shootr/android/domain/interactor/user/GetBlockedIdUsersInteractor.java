package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FollowRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class GetBlockedIdUsersInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FollowRepository localFollowRepository;
    private final FollowRepository remoteFollowRepository;
    private final SessionRepository sessionRepository;

    private Callback<List<String>> callback;
    private ErrorCallback errorCallback;

    @Inject public GetBlockedIdUsersInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local FollowRepository localFollowRepository, @Remote FollowRepository remoteFollowRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFollowRepository = localFollowRepository;
        this.remoteFollowRepository = remoteFollowRepository;
        this.sessionRepository = sessionRepository;
    }

    public void loadBlockedIdUsers(Callback<List<String>> callback, ErrorCallback errorCallback){
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadLocalBlockedIdUsers();
        loadRemoteBlockedIdUsers();
    }

    private void loadLocalBlockedIdUsers() {
        List<String> blockedIdUsers = localFollowRepository.getBlockedIdUsers();
        if (blockedIdUsers != null) {
            notifyResult(blockedIdUsers);
        }
    }

    private void loadRemoteBlockedIdUsers() {
        try {
            notifyResult(remoteFollowRepository.getBlockedIdUsers());
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
