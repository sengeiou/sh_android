package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.interactor.Interactor;
import java.io.File;
import javax.inject.Inject;

public class PostNewShotInStreamInteractor extends PostNewShotInteractor {

    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;

    @Inject public PostNewShotInStreamInteractor(
      com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler, com.shootr.mobile.domain.repository.SessionRepository sessionRepository,
      @com.shootr.mobile.domain.repository.Local
      com.shootr.mobile.domain.repository.StreamRepository localStreamRepository, @com.shootr.mobile.domain.service.dagger.Background
    com.shootr.mobile.domain.service.ShotSender shotSender) {
        super(postExecutionThread, interactorHandler, sessionRepository, shotSender);
        this.sessionRepository = sessionRepository;
        this.localStreamRepository = localStreamRepository;
    }

    public void postNewShotInStream(String comment, File image, Interactor.CompletedCallback callback, Interactor.ErrorCallback errorCallback) {
        super.postNewShot(comment, image, callback, errorCallback);
    }

    @Override protected void fillShotStreamInfo(com.shootr.mobile.domain.Shot shot) {
        com.shootr.mobile.domain.Stream stream = currentVisibleStream();
        if (stream != null) {
            com.shootr.mobile.domain.Shot.ShotStreamInfo eventInfo = new com.shootr.mobile.domain.Shot.ShotStreamInfo();
            eventInfo.setIdStream(stream.getId());
            eventInfo.setStreamTitle(stream.getTitle());
            eventInfo.setStreamShortTitle(stream.getShortTitle());
            shot.setStreamInfo(eventInfo);
        }
    }

    private com.shootr.mobile.domain.Stream currentVisibleStream() {
        String visibleStreamId = sessionRepository.getCurrentUser().getIdWatchingStream();
        if (visibleStreamId != null) {
            return localStreamRepository.getStreamById(visibleStreamId);
        } else {
            return null;
        }
    }
}
