package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.TimelineException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.service.shot.ShootrTimelineService;

import java.util.List;

import javax.inject.Inject;

public class RefreshActivityTimelineInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrTimelineService shootrTimelineService;

    private Callback<Timeline> callback;
    private ErrorCallback errorCallback;

    @Inject public RefreshActivityTimelineInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
                                                     ShootrTimelineService shootrTimelineService) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrTimelineService = shootrTimelineService;
    }

    public void refreshMainTimeline(Callback<Timeline> callback, ErrorCallback errorCallback) {
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
            Timeline activityTimeline = filterActivityTimeline(timelines);
            notifyLoaded(activityTimeline);
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private Timeline filterActivityTimeline(List<Timeline> timelines) {
        for (Timeline timeline : timelines) {
            if (timeline.getParameters().getEventId() == null) {
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
