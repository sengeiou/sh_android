package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetNicersInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository remoteUserRepository;
    private final PostExecutionThread postExecutionThread;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private String idShot;

    @Inject public GetNicersInteractor(InteractorHandler interactorHandler,
      @Remote UserRepository remoteUserRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainNicers(String idShot, Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.idShot = idShot;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            List<User> allParticipants = obtainNicersList();
            notifyLoaded(allParticipants);
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private List<User> obtainNicersList() {
        //TODO: obtain nicers
        return new ArrayList<>();
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
