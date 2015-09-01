package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.UserRepository;
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

    @Inject public FindParticipantsInteractor(InteractorHandler interactorHandler, @Remote UserRepository remoteUserRepository,
      PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainAllParticipants(String idStream, String query, Callback<List<User>> callback, ErrorCallback errorCallback) {
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
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
