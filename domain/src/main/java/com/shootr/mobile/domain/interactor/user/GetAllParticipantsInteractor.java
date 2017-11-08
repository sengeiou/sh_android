package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class GetAllParticipantsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository remoteUserRepository;
    private final PostExecutionThread postExecutionThread;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private String idStream;
    private Long timestamp;

    private Boolean isPaginating;

    @Inject public GetAllParticipantsInteractor(InteractorHandler interactorHandler,
      @Remote UserRepository remoteUserRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainAllParticipants(String idStream, Long timestamp, Boolean isPaginating,
      Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.timestamp = timestamp;
        this.isPaginating = isPaginating;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            List<User> allParticipants =
                remoteUserRepository.getAllParticipants(idStream, timestamp);
            notifyLoaded(allParticipants);
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
