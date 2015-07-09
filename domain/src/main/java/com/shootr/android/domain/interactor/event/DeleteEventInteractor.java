package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class DeleteEventInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventRepository remoteEventRepository;
    private final EventRepository localEventRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private String idEvent;
    private CompletedCallback completedCallback;
    private ErrorCallback errorCallback;

    @Inject
    public DeleteEventInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote EventRepository remoteEventRepository,
      @Local EventRepository localEventRepository,
      SessionRepository sessionRepository,
      @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteEventRepository = remoteEventRepository;
        this.localEventRepository = localEventRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void deleteEvent(String idEvent, CompletedCallback completedCallback, ErrorCallback errorCallback) {
        this.idEvent = checkNotNull(idEvent);
        this.completedCallback = completedCallback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        try {
            remoteEventRepository.deleteEvent(idEvent);
            localEventRepository.deleteEvent(idEvent);

            User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
            currentUser.setIdWatchingEvent(null);
            currentUser.setWatchingEventTitle(null);

            localUserRepository.putUser(currentUser);
            remoteUserRepository.putUser(currentUser);
            sessionRepository.setCurrentUser(currentUser);
            notifyCompleted();
        } catch (DeleteEventNotAllowedException deleteNotAllowedError) {
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
