package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetContributorsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final PostExecutionThread postExecutionThread;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private String idStream;

    @Inject public GetContributorsInteractor(InteractorHandler interactorHandler, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainContributors(String idStream, Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        List<User> contributors = obtainLocalContributors();
        if (contributors.isEmpty()) {
            contributors = obtainRemoteContributors();
        }
        notifyLoaded(contributors);
    }

    private List<User> obtainRemoteContributors() {
        return Collections.emptyList();
    }

    private List<User> obtainLocalContributors() {
        return Collections.emptyList();
    }

    private void notifyLoaded(final List<User> results) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(results);
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
