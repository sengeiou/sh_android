package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import javax.inject.Inject;

public class ContributorInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final ContributorRepository contributorRepository;
    private final PostExecutionThread postExecutionThread;

    private String idStream;
    private String idUser;
    private Boolean isAdding;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject
    public ContributorInteractor(InteractorHandler interactorHandler, ContributorRepository contributorRepository,
      PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.contributorRepository = contributorRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void manageContributor(String idStream, String idUser, Boolean isAdding, CompletedCallback callback,
      ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.idUser = idUser;
        this.isAdding = isAdding;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        if(isAdding) {
            addRemoteContributor(idStream, idUser);
        } else {
            removeRemoteContributor(idStream, idUser);
        }
    }

    private void addRemoteContributor(String idStream, String idUser) {
        try {
            contributorRepository.addContributor(idStream, idUser);
            notifyCompleted();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void removeRemoteContributor(String idStream, String idUser) {
        try {
            contributorRepository.removeContributor(idStream, idUser);
            notifyCompleted();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
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
