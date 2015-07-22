package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

public class PostNewShotInEventInteractor extends PostNewShotInteractor {

    private final SessionRepository sessionRepository;
    private final StreamRepository localStreamRepository;

    @Inject public PostNewShotInEventInteractor(PostExecutionThread postExecutionThread, InteractorHandler interactorHandler,
      SessionRepository sessionRepository, @Local StreamRepository localStreamRepository,
      @Background ShotSender shotSender) {
        super(postExecutionThread, interactorHandler, sessionRepository, shotSender);
        this.sessionRepository = sessionRepository;
        this.localStreamRepository = localStreamRepository;
    }

    public void postNewShotInEvent(String comment, File image, CompletedCallback callback, ErrorCallback errorCallback) {
        super.postNewShot(comment, image, callback, errorCallback);
    }

    @Override protected void fillShotEventInfo(Shot shot) {
        Stream stream = currentVisibleEvent();
        if (stream != null) {
            Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
            eventInfo.setIdEvent(stream.getId());
            eventInfo.setEventTitle(stream.getTitle());
            eventInfo.setEventTag(stream.getTag());
            shot.setEventInfo(eventInfo);
        }
    }

    private Stream currentVisibleEvent() {
        String visibleEventId = sessionRepository.getCurrentUser().getIdWatchingEvent();
        if (visibleEventId != null) {
            return localStreamRepository.getStreamById(visibleEventId);
        } else {
            return null;
        }
    }
}
