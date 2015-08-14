package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.DeleteStreamNotAllowedException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class DeleteStreamInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository remoteStreamRepository;
    private final StreamRepository localStreamRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private String idStream;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public DeleteStreamInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote StreamRepository remoteStreamRepository, @Local StreamRepository localStreamRepository,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localStreamRepository = localStreamRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void deleteStream(String idStream, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idStream = checkNotNull(idStream);
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        try {
            remoteStreamRepository.deleteStream(idStream);
            localStreamRepository.deleteStream(idStream);

            User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
            currentUser.setIdWatchingStream(null);
            currentUser.setWatchingStreamTitle(null);
            currentUser.setJoinStreamDate(null);

            localUserRepository.putUser(currentUser);
            remoteUserRepository.putUser(currentUser);
            sessionRepository.setCurrentUser(currentUser);
            notifyCompleted();
        } catch (DeleteStreamNotAllowedException deleteNotAllowedError) {
            notifyError(deleteNotAllowedError);
        } catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    private void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                completedCallback.onCompleted();
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