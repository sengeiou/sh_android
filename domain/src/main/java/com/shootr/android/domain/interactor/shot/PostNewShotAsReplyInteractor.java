package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ShotRemovedException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class PostNewShotAsReplyInteractor extends PostNewShotInteractor {

    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;
    private String replyParentId;
    private Shot parentShot;

    @Inject public PostNewShotAsReplyInteractor(PostExecutionThread postExecutionThread, InteractorHandler interactorHandler,
      SessionRepository sessionRepository, @Background ShotSender shotSender, @Local ShotRepository localShotRepository,
      @Remote ShotRepository remoteShotRepository) {
        super(postExecutionThread, interactorHandler, sessionRepository, shotSender);
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void postNewShotAsReply(String comment, File image, String replyParentId, CompletedCallback callback, ErrorCallback errorCallback) {
        this.replyParentId = replyParentId;
        super.postNewShot(comment, image, callback, errorCallback);
    }

    @Override protected void fillShotContextualInfo(Shot shot) {
        super.fillShotContextualInfo(shot);
        try {
            fillReplyInfo(shot);
        } catch (NullPointerException error) {
            // Swallow
        }
    }

    @Override protected void fillShotStreamInfo(Shot shot) {
        Shot parentShot = getParentShot();
        checkNotNull(parentShot, "Parent shot not found with id=%s", replyParentId);
        shot.setStreamInfo(parentShot.getStreamInfo());
    }

    private void fillReplyInfo(Shot shot) {
        Shot parentShot = getParentShot();
        shot.setParentShotId(parentShot.getIdShot());
        shot.setParentShotUserId(parentShot.getUserInfo().getIdUser());
        shot.setParentShotUsername(parentShot.getUserInfo().getUsername());
    }

    private Shot getParentShot() {
        try {
            parentShot = localShotRepository.getShot(replyParentId);
            if (parentShot == null) {
                parentShot = remoteShotRepository.getShot(replyParentId);
            }
            return parentShot;
        } catch (ShotRemovedException error) {
            /* nothing */
        }
        return null;
    }
}
