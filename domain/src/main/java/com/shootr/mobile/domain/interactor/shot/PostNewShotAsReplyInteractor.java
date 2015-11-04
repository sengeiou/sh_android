package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class PostNewShotAsReplyInteractor extends PostNewShotInteractor {

    private final com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    private final com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;
    private String replyParentId;
    private com.shootr.mobile.domain.Shot parentShot;

    @Inject public PostNewShotAsReplyInteractor(PostExecutionThread postExecutionThread, com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      com.shootr.mobile.domain.repository.SessionRepository sessionRepository, @Background
    com.shootr.mobile.domain.service.ShotSender shotSender, @Local
    com.shootr.mobile.domain.repository.ShotRepository localShotRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository) {
        super(postExecutionThread, interactorHandler, sessionRepository, shotSender);
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void postNewShotAsReply(String comment, File image, String replyParentId, CompletedCallback callback, ErrorCallback errorCallback) {
        this.replyParentId = replyParentId;
        super.postNewShot(comment, image, callback, errorCallback);
    }

    @Override protected void fillShotContextualInfo(com.shootr.mobile.domain.Shot shot) {
        super.fillShotContextualInfo(shot);
        fillReplyInfo(shot);
    }

    @Override protected void fillShotStreamInfo(com.shootr.mobile.domain.Shot shot) {
        com.shootr.mobile.domain.Shot parentShot = getParentShot();
        checkNotNull(parentShot, "Parent shot not found with id=%s", replyParentId);
        shot.setStreamInfo(parentShot.getStreamInfo());
    }

    private void fillReplyInfo(com.shootr.mobile.domain.Shot shot) {
        com.shootr.mobile.domain.Shot parentShot = getParentShot();
        shot.setParentShotId(parentShot.getIdShot());
        shot.setParentShotUserId(parentShot.getUserInfo().getIdUser());
        shot.setParentShotUsername(parentShot.getUserInfo().getUsername());
    }

    private com.shootr.mobile.domain.Shot getParentShot() {
        if (parentShot == null) {
            parentShot = localShotRepository.getShot(replyParentId);
            if (parentShot == null) {
                parentShot = remoteShotRepository.getShot(replyParentId);
            }
        }
        return parentShot;
    }
}
