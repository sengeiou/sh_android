package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.TimelineException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.service.shot.ShootrTimelineService;

import java.util.List;
import javax.inject.Inject;

public class RefreshEventTimelineInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrTimelineService shootrTimelineService;

    private Callback<Timeline> callback;
    private ErrorCallback errorCallback;

    @Inject public RefreshEventTimelineInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
                                                  ShootrTimelineService shootrTimelineService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrTimelineService = shootrTimelineService;
    }

    public void refreshEventTimeline(Callback<Timeline> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        executeSynchronized();
    }

    private synchronized void executeSynchronized() {
        try {
            List<Timeline> timelines = shootrTimelineService.refreshTimelines();
            Timeline eventTimeline = filterEventTimeline(timelines);
            notifyLoaded(eventTimeline);
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private Timeline filterEventTimeline(List<Timeline> timelines) {
        for (Timeline timeline : timelines) {
            if (timeline.getParameters().getEventId() != null) {
                return timeline;
            }
        }
        throw new TimelineException(String.format("Timeline with event not found in timeline list <%s>", timelines.toString()));
    }

    //region Result
    private void notifyLoaded(final Timeline timeline) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(timeline);
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
    //endregion
}
