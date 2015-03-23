package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Background;
import java.io.File;
import java.util.Date;
import javax.inject.Inject;

public class PostNewShotInteractor implements Interactor {

    private final PostExecutionThread postExecutionThread;
    private final InteractorHandler interactorHandler;
    private final SessionRepository sessionRepository;
    private final EventRepository localEventRepository;
    private final ShotSender shotSender;
    private String comment;
    private File imageFile;
    private Callback callback;
    private InteractorErrorCallback errorCallback;

    @Inject public PostNewShotInteractor(PostExecutionThread postExecutionThread, InteractorHandler interactorHandler,
      SessionRepository sessionRepository, @Local EventRepository localEventRepository,
      @Background ShotSender shotSender) {
        this.postExecutionThread = postExecutionThread;
        this.interactorHandler = interactorHandler;
        this.sessionRepository = sessionRepository;
        this.localEventRepository = localEventRepository;
        this.shotSender = shotSender;
    }

    public void postNewShot(String comment, File image, Callback callback, InteractorErrorCallback errorCallback) {
        this.comment = comment;
        this.imageFile = image;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Shot shotToPublish = createShotFromParameters();
        try {
            notifyReadyToSend();
            sendShotToServer(shotToPublish);
        } catch (DomainValidationException validationError) {
            notifyError(validationError);
        }
    }

    private void sendShotToServer(Shot shot) throws ServerCommunicationException {
        shotSender.sendShot(shot, imageFile);
    }

    private Shot createShotFromParameters() {
        Shot shot = new Shot();
        shot.setComment(filterComment(comment));
        shot.setPublishDate(new Date());
        fillShotContextualInfo(shot);
        return shot;
    }

    private String filterComment(String comment) {
        if (comment != null && comment.isEmpty()) {
            return null;
        }
        return comment;
    }

    private void fillShotContextualInfo(Shot shot) {
        fillShotUserInfo(shot);
        fillShotEventInfo(shot);
    }

    private void fillShotUserInfo(Shot shot) {
        User currentUser = sessionRepository.getCurrentUser();
        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();

        userInfo.setIdUser(currentUser.getIdUser());
        userInfo.setAvatar(currentUser.getPhoto());
        userInfo.setUsername(currentUser.getUsername());

        shot.setUserInfo(userInfo);
    }

    private void fillShotEventInfo(Shot shot) {
        Event currentVisibleEvent = currentVisibleEvent();
        if (currentVisibleEvent != null) {
            Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
            eventInfo.setIdEvent(currentVisibleEvent.getId());
            eventInfo.setEventTitle(currentVisibleEvent.getTitle());
            eventInfo.setEventTag(currentVisibleEvent.getTag());
            shot.setEventInfo(eventInfo);
        }
    }

    private Event currentVisibleEvent() {
        Long visibleEventId = sessionRepository.getCurrentUser().getVisibleEventId();
        if (visibleEventId != null) {
            return localEventRepository.getEventById(visibleEventId);
        } else {
            return null;
        }
    }

    private void notifyReadyToSend() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded();
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

    public static interface Callback {

        void onLoaded();
    }
}
