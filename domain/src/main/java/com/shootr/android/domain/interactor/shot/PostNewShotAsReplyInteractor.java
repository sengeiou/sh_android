package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

public class PostNewShotAsReplyInteractor extends PostNewShotInteractor {

    private final ShotRepository shotRepository;
    private String replyParentId;
    private Shot parentShot;

    @Inject public PostNewShotAsReplyInteractor(PostExecutionThread postExecutionThread, InteractorHandler interactorHandler,
      SessionRepository sessionRepository, @Background ShotSender shotSender, @Local ShotRepository shotRepository) {
        super(postExecutionThread, interactorHandler, sessionRepository, shotSender);
        this.shotRepository = shotRepository;
    }

    public void postNewShotAsReply(String comment, File image, String replyParentId, CompletedCallback callback, ErrorCallback errorCallback) {
        this.replyParentId = replyParentId;
        super.postNewShot(comment, image, callback, errorCallback);
    }

    @Override protected void fillShotContextualInfo(Shot shot) {
        super.fillShotContextualInfo(shot);
        fillReplyInfo(shot);
    }

    @Override protected void fillShotEventInfo(Shot shot) {
        Shot parentShot = getParentShot();
        if (parentShot == null) {
            throw new IllegalArgumentException(String.format("Parent shot not found with id=%d", replyParentId));
        }
        shot.setEventInfo(parentShot.getEventInfo());
    }

    @Override protected String getRootType() {
        return getParentShot().getRootType();
    }

    private void fillReplyInfo(Shot shot) {
        Shot parentShot = getParentShot();
        shot.setParentShotId(parentShot.getIdShot());
        shot.setParentShotUserId(parentShot.getUserInfo().getIdUser());
        shot.setParentShotUsername(parentShot.getUserInfo().getUsername());
    }

    private Shot getParentShot() {
        if (parentShot == null) {
            parentShot = shotRepository.getShot(replyParentId);
        }
        return parentShot;
    }
}
