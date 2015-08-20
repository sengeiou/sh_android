package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.User;
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

public class RestoreStreamInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamRepository remoteStreamRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private String idStream;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public RestoreStreamInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote StreamRepository remoteStreamRepository,SessionRepository sessionRepository, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteStreamRepository = remoteStreamRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void restoreStream(String idStream, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idStream = checkNotNull(idStream);
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        try {
            Stream stream = remoteStreamRepository.getStreamById(idStream);
            stream.setRemoved(false);

            remoteStreamRepository.putStream(stream);

            User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());

            localUserRepository.putUser(currentUser);
            remoteUserRepository.putUser(currentUser);
            sessionRepository.setCurrentUser(currentUser);
            notifyCompleted();
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
