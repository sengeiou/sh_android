package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetFollowingInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private boolean onlySynchronize = false;

    @Inject public GetFollowingInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void obtainPeople(Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    public void synchronizeFollow() {
        this.onlySynchronize = true;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        if (onlySynchronize) {
            remoteUserRepository.synchronizeFollow();
        } else {
            tryObtainingLocalPeople();
        }
    }

    private void tryObtainingLocalPeople() {
        List<User> userList = localUserRepository.getPeople();
        if (!userList.isEmpty()) {
            notifyResult(userList);
        } else {
            obtainRemotePeople();
        }
    }

    private void obtainRemotePeople() {
        try {
            List<User> userList = remoteUserRepository.getPeople();
            if (userList != null) {
                notifyResult(userList);
            } else {
                notifyResult(Collections.<User>emptyList());
            }
        } catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private void notifyResult(final List<User> suggestedPeople) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                if (!onlySynchronize) {
                    callback.onLoaded(suggestedPeople);
                }
            }
        });
    }

    private void notifyError(final ShootrException error) {
        if (!onlySynchronize) {
            postExecutionThread.post(new Runnable() {
                @Override public void run() {
                    errorCallback.onError(error);
                }
            });
        }
    }
}
