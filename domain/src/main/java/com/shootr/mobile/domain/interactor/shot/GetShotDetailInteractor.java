package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotDetail;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetShotDetailInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;

    private String idShot;
    private Callback<ShotDetail> callback;
    private ErrorCallback errorCallback;

    @Inject public GetShotDetailInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void loadShotDetail(String idShot, Callback<ShotDetail> callback, ErrorCallback errorCallback) {
        this.idShot = idShot;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            ShotDetail localShotDetail = localShotRepository.getShotDetail(idShot);
            if (localShotDetail != null) {
                notifyLoaded(reoderReplies(localShotDetail));
            }
            ShotDetail remoteShotDetail = remoteShotRepository.getShotDetail(idShot);
            notifyLoaded(reoderReplies(remoteShotDetail));
            if (localShotDetail != null) {
                localShotRepository.putShot(remoteShotDetail.getShot());
            }
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private ShotDetail reoderReplies(ShotDetail shotDetail) {
        List<Shot> unorderedReplies = shotDetail.getReplies();
        List<Shot> reorderedReplies = orderShots(unorderedReplies);
        shotDetail.setReplies(reorderedReplies);
        return shotDetail;
    }

    private List<Shot> orderShots(List<Shot> replies) {
        Collections.sort(replies, new Shot.NewerBelowComparator());
        return replies;
    }

    private void notifyLoaded(final ShotDetail shotDetail) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(shotDetail);
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
