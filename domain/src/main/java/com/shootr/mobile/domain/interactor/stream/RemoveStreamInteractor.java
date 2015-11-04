package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.utils.Preconditions;
import javax.inject.Inject;

public class RemoveStreamInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private final com.shootr.mobile.domain.repository.UserRepository localUserRepository;
    private final com.shootr.mobile.domain.repository.UserRepository remoteUserRepository;
    private String idStream;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public RemoveStreamInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      @com.shootr.mobile.domain.repository.Local
      com.shootr.mobile.domain.repository.StreamRepository localStreamRepository, @com.shootr.mobile.domain.repository.Remote
    com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository, com.shootr.mobile.domain.repository.SessionRepository sessionRepository, @com.shootr.mobile.domain.repository.Local
    com.shootr.mobile.domain.repository.UserRepository localUserRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void removeStream(String idStream, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idStream = Preconditions.checkNotNull(idStream);
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        try {
            remoteStreamRepository.removeStream(idStream);
            localStreamRepository.removeStream(idStream);

            com.shootr.mobile.domain.User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
            removeWatching(currentUser);
            sessionRepository.setCurrentUser(currentUser);
            localUserRepository.updateWatch(currentUser);
            remoteUserRepository.updateWatch(currentUser);

            notifyCompleted();
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private void removeWatching(com.shootr.mobile.domain.User currentUser) {
        currentUser.setIdWatchingStream(null);
        currentUser.setWatchingStreamTitle(null);
        currentUser.setJoinStreamDate(null);
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                completedCallback.onCompleted();
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
}