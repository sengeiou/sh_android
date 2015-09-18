package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotType;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShotRemovedException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Background;
import com.shootr.android.domain.service.shot.DeletedShotException;
import java.io.File;
import java.util.Date;

public abstract class PostNewShotInteractor implements Interactor {

    private final PostExecutionThread postExecutionThread;
    private final InteractorHandler interactorHandler;
    private final SessionRepository sessionRepository;
    private final ShotSender shotSender;
    private String comment;
    private File imageFile;
    private CompletedCallback callback;
    private ErrorCallback errorCallback;

    public PostNewShotInteractor(PostExecutionThread postExecutionThread, InteractorHandler interactorHandler,
      SessionRepository sessionRepository, @Background ShotSender shotSender) {
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
            Shot shotToPublish = createShotFromParameters();
            notifyReadyToSend();
            sendShotToServer(shotToPublish);
        } catch (ShotRemovedException error) {
            notifyError(new DeletedShotException(error));
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private void sendShotToServer(Shot shot) throws ServerCommunicationException, ShotRemovedException {
        shotSender.sendShot(shot, imageFile);
    }

    private Shot createShotFromParameters() {
        Shot shot = new Shot();
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

    protected void fillShotContextualInfo(Shot shot) {
        fillShotUserInfo(shot);
        try {
            fillShotStreamInfo(shot);
        } catch (NullPointerException error) {
            notifyError(new DeletedShotException(error));
        }
    }

    private void fillShotUserInfo(Shot shot) {
        User currentUser = sessionRepository.getCurrentUser();
        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();

        userInfo.setIdUser(currentUser.getIdUser());
        userInfo.setAvatar(currentUser.getPhoto());
        userInfo.setUsername(currentUser.getUsername());

        shot.setUserInfo(userInfo);
    }

    protected abstract void fillShotStreamInfo(Shot shot);

    private void notifyReadyToSend() {
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
