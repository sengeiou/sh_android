package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetAllParticipantsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final PostExecutionThread postExecutionThread;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private String idStream;

    @Inject public GetAllParticipantsInteractor(InteractorHandler interactorHandler,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainAllParticipants(String idStream, Callback<List<User>> callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            List<User> allParticipants = obtainAllParticipantsList();

            notifyLoaded(allParticipants);
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private List<User> obtainAllParticipantsList() {
        List<User> following = remoteUserRepository.getPeople();
        List<User> participants = remoteUserRepository.getAllParticipants(idStream);

        participants.removeAll(following);

        List<User> allParticipants = new ArrayList<>();
        allParticipants.addAll(following);
        allParticipants.addAll(participants);
        return allParticipants;
    }

    private void notifyLoaded(final List<User> results) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
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
