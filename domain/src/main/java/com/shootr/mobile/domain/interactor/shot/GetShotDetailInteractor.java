package com.shootr.mobile.domain.interactor.shot;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetShotDetailInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.ShotRepository localShotRepository;
    private final com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository;

    private String idShot;
    private Callback<com.shootr.mobile.domain.ShotDetail> callback;
    private ErrorCallback errorCallback;

    @Inject public GetShotDetailInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      @com.shootr.mobile.domain.repository.Local com.shootr.mobile.domain.repository.ShotRepository localShotRepository, @com.shootr.mobile.domain.repository.Remote
    com.shootr.mobile.domain.repository.ShotRepository remoteShotRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
    }

    public void loadShotDetail(String idShot, Callback<com.shootr.mobile.domain.ShotDetail> callback, ErrorCallback errorCallback) {
        this.idShot = idShot;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        try {
            com.shootr.mobile.domain.ShotDetail localShotDetail = localShotRepository.getShotDetail(idShot);
            if (localShotDetail != null) {
                notifyLoaded(reoderReplies(localShotDetail));
            }
            com.shootr.mobile.domain.ShotDetail remoteShotDetail = remoteShotRepository.getShotDetail(idShot);
            notifyLoaded(reoderReplies(remoteShotDetail));
            if (localShotDetail != null) {
                localShotRepository.putShot(remoteShotDetail.getShot());
            }
        } catch (com.shootr.mobile.domain.exception.ShootrException error) {
            notifyError(error);
        }
    }

    private com.shootr.mobile.domain.ShotDetail reoderReplies(com.shootr.mobile.domain.ShotDetail shotDetail) {
        List<com.shootr.mobile.domain.Shot> unorderedReplies = shotDetail.getReplies();
        List<com.shootr.mobile.domain.Shot> reorderedReplies = orderShots(unorderedReplies);
        shotDetail.setReplies(reorderedReplies);
        return shotDetail;
    }

    private List<com.shootr.mobile.domain.Shot> orderShots(List<com.shootr.mobile.domain.Shot> replies) {
        Collections.sort(replies, new com.shootr.mobile.domain.Shot.NewerBelowComparator());
        return replies;
    }

    private void notifyLoaded(final com.shootr.mobile.domain.ShotDetail shotDetail) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(shotDetail);
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
