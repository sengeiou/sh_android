package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.ShotSender;
import com.shootr.android.domain.service.dagger.Background;
import java.io.File;
import javax.inject.Inject;

public class PostNewShotInEventInteractor extends PostNewShotInteractor {

    private final SessionRepository sessionRepository;
    private final EventRepository localEventRepository;

    @Inject public PostNewShotInEventInteractor(PostExecutionThread postExecutionThread, InteractorHandler interactorHandler,
      SessionRepository sessionRepository, @Local EventRepository localEventRepository,
      @Background ShotSender shotSender) {
        super(postExecutionThread, interactorHandler, sessionRepository, shotSender);
        this.sessionRepository = sessionRepository;
        this.localEventRepository = localEventRepository;
    }

    public void postNewShotInEvent(String comment, File image, CompletedCallback callback, ErrorCallback errorCallback) {
        super.postNewShot(comment, image, callback, errorCallback);
    }

    @Override protected void fillShotEventInfo(Shot shot) {
        Event event = currentVisibleEvent();
        if (event != null) {
            Shot.ShotEventInfo eventInfo = new Shot.ShotEventInfo();
            eventInfo.setIdEvent(event.getId());
            eventInfo.setEventTitle(event.getTitle());
            eventInfo.setEventTag(event.getTag());
            shot.setEventInfo(eventInfo);
        }
    }

    private Event currentVisibleEvent() {
        String visibleEventId = sessionRepository.getCurrentUser().getVisibleEventId();
        if (visibleEventId != null) {
            return localEventRepository.getEventById(Long.parseLong(visibleEventId));
        } else {
            return null;
        }
    }
}
