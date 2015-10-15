package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

public class PostNewShotInStreamInteractor extends PostNewShotInteractor {

    private final SessionRepository sessionRepository;
    private final StreamRepository localStreamRepository;

    @Inject public PostNewShotInStreamInteractor(PostExecutionThread postExecutionThread,
      InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Local StreamRepository localStreamRepository, @Background ShotSender shotSender) {
        super(postExecutionThread, interactorHandler, sessionRepository, shotSender);
        this.sessionRepository = sessionRepository;
        this.localStreamRepository = localStreamRepository;
    }

    public void postNewShotInStream(String comment, File image, CompletedCallback callback, ErrorCallback errorCallback) {
        super.postNewShot(comment, image, callback, errorCallback);
    }

    @Override protected void fillShotStreamInfo(Shot shot) {
        Stream stream = currentVisibleStream();
        if (stream != null) {
            Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
            eventInfo.setIdStream(stream.getId());
            eventInfo.setStreamTitle(stream.getTitle());
            eventInfo.setStreamShortTitle(stream.getShortTitle());
            shot.setStreamInfo(eventInfo);
        }
    }

    private Stream currentVisibleStream() {
        String visibleStreamId = sessionRepository.getCurrentUser().getIdWatchingStream();
        if (visibleStreamId != null) {
            return localStreamRepository.getStreamById(visibleStreamId);
        } else {
            return null;
        }
    }
}
