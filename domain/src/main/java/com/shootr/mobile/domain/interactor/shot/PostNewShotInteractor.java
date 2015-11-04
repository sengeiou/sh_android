package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.ShotType;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.service.dagger.Background;
import java.io.File;
import java.util.Date;

public abstract class PostNewShotInteractor implements Interactor {

    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private final com.shootr.mobile.domain.service.ShotSender shotSender;
    private String comment;
    private File imageFile;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    public PostNewShotInteractor(PostExecutionThread postExecutionThread, com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      com.shootr.mobile.domain.repository.SessionRepository sessionRepository, @Background
    com.shootr.mobile.domain.service.ShotSender shotSender) {
        this.postExecutionThread = postExecutionThread;
        this.interactorHandler = interactorHandler;
        this.sessionRepository = sessionRepository;
        this.shotSender = shotSender;
    }

    protected void postNewShot(String comment, File image,
      CompletedCallback callback, ErrorCallback errorCallback) {
        this.comment = comment;
        this.imageFile = image;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            com.shootr.mobile.domain.Shot shotToPublish = createShotFromParameters();
            notifyReadyToSend();
            sendShotToServer(shotToPublish);
        } catch (com.shootr.mobile.domain.exception.ShootrException error) {
            notifyError(error);
        }
    }

    private void sendShotToServer(com.shootr.mobile.domain.Shot shot) {
        shotSender.sendShot(shot, imageFile);
    }

    private com.shootr.mobile.domain.Shot createShotFromParameters() {
        com.shootr.mobile.domain.Shot shot = new com.shootr.mobile.domain.Shot();
        shot.setComment(filterComment(comment));
        shot.setPublishDate(new Date());
        fillShotContextualInfo(shot);
        shot.setType(ShotType.COMMENT);
        return shot;
    }

    private String filterComment(String comment) {
        if (comment != null && comment.isEmpty()) {
            return null;
        }
        return comment;
    }

    protected void fillShotContextualInfo(com.shootr.mobile.domain.Shot shot) {
        fillShotUserInfo(shot);
        fillShotStreamInfo(shot);
    }

    private void fillShotUserInfo(com.shootr.mobile.domain.Shot shot) {
        User currentUser = sessionRepository.getCurrentUser();
        com.shootr.mobile.domain.Shot.ShotUserInfo userInfo = new com.shootr.mobile.domain.Shot.ShotUserInfo();

        userInfo.setIdUser(currentUser.getIdUser());
        userInfo.setAvatar(currentUser.getPhoto());
        userInfo.setUsername(currentUser.getUsername());

        shot.setUserInfo(userInfo);
    }

    protected abstract void fillShotStreamInfo(com.shootr.mobile.domain.Shot shot);

    private void notifyReadyToSend() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }

}
