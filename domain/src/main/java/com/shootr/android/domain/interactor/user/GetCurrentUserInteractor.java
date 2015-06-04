package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.repository.SessionRepository;
import javax.inject.Inject;

public class GetCurrentUserInteractor implements Interactor {

    private final SessionRepository sessionRepository;
    private final PostExecutionThread postExecutionThread;
    private Callback<User> callback;
    private ErrorCallback errorCallback;

    @Inject public GetCurrentUserInteractor(SessionRepository sessionRepository,
      PostExecutionThread postExecutionThread) {
        this.sessionRepository = sessionRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void getCurrentUser(Callback<User> callback, ErrorCallback errorCallback) throws Throwable {
        this.callback = callback;
        this.errorCallback = errorCallback;
        execute();
    }

    @Override public void execute() throws Throwable {
        loadSessionUser();
    }

    private void loadSessionUser() {
        try{
            notifyResult(sessionRepository.getCurrentUser());
        }catch (ShootrException error){
            notifyError(error);
        }
    }

    private void notifyResult(final User user) {
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
