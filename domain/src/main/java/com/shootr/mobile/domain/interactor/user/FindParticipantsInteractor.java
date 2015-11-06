package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class FindParticipantsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository remoteUserRepository;
    private final PostExecutionThread postExecutionThread;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private String idStream;
    private String query;

    @Inject
    public FindParticipantsInteractor(InteractorHandler interactorHandler, @Remote UserRepository remoteUserRepository,
      PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainAllParticipants(String idStream, String query, Callback<List<User>> callback,
      ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.query = query;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            notifyLoaded(remoteUserRepository.findParticipants(idStream, query));
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
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
