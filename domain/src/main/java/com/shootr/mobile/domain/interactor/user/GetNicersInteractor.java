package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.Nicer;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.NicerRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetNicersInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final NicerRepository nicerRepository;
    private final PostExecutionThread postExecutionThread;

    private Callback<List<User>> callback;
    private ErrorCallback errorCallback;
    private String idShot;

    @Inject public GetNicersInteractor(InteractorHandler interactorHandler,
      NicerRepository nicerRepository, PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.nicerRepository = nicerRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void obtainNicersWithUser(String idShot, Callback<List<User>> callback, ErrorCallback errorCallback) {
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
        List<User> users = new ArrayList<>();
        List<Nicer> nicers = nicerRepository.getNicersWithUser(idShot);

        if(nicers != null && !nicers.isEmpty()) {
            for (Nicer nicer : nicers) {
                users.add(nicer.getUser());
            }
        }

        return users;
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
