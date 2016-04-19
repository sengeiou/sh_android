package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import javax.inject.Inject;

public class AddContributorInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final ContributorRepository contributorRepository;
    private final PostExecutionThread postExecutionThread;

    private String idStream;
    private String idUser;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    @Inject
    public AddContributorInteractor(InteractorHandler interactorHandler, ContributorRepository contributorRepository,
      PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.contributorRepository = contributorRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void addContributor(String idStream, String idUser, CompletedCallback callback,
      ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.idUser = idUser;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        addRemoteContributor(idStream, idUser);
    }

    private void addRemoteContributor(String idStream, String idUser) {
        try {
            contributorRepository.addContributor(idStream, idUser);
            notifyCompleted();
        } catch (Exception error) {
            //TODO: notify error
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
