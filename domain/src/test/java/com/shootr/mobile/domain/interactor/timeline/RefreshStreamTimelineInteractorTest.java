package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshStreamTimelineInteractorTest {

    public static final String ID_STREAM = "idStream";
    private static final Boolean NOT_PAUSED = false;
    private static final Long LAST_REFRESH_DATE = 0L;
    private static final Boolean REAL_TIME = true;
    private static final Boolean PAUSED = true;
    public static final boolean IS_REAL_TIME = true;
    public static final boolean IS_NOT_REAL_TIME = false;
    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock ShootrTimelineService shootrTimelineService;
    @Mock LocaleProvider localeProvider;
    private RefreshStreamTimelineInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.interactor = new RefreshStreamTimelineInteractor(interactorHandler,
          postExecutionThread,
          shootrTimelineService,
          localeProvider);
    }

    @Test public void shouldCallbackStreamTimelineWhenServiceReturnsTimelineForStream() throws Exception {
        when(shootrTimelineService.refreshTimelinesForStream(ID_STREAM,
          NOT_PAUSED)).thenReturn(timelineForStream());

        interactor.refreshStreamTimeline(ID_STREAM, LAST_REFRESH_DATE, REAL_TIME, spyCallback, errorCallback);

        verify(spyCallback).onLoaded(timelineForStream());
    }

    @Test public void shouldRefreshTimelineForStreamAndWhitRealTime() throws Exception {
        interactor.incrementCalls();

        interactor.refreshStreamTimeline(ID_STREAM, new Date().getTime(), NOT_PAUSED, spyCallback, errorCallback);

        verify(shootrTimelineService).refreshTimelinesForStream(ID_STREAM, IS_REAL_TIME);
    }

    @Test public void shouldRefreshTimelineForStreamWhenPausedWithoutRealTime() throws Exception {
        interactor.refreshStreamTimeline(ID_STREAM, 0L, PAUSED, spyCallback, errorCallback);

        verify(shootrTimelineService).refreshTimelinesForStream(ID_STREAM, IS_NOT_REAL_TIME);
    }

    @Test public void shouldRefreshTimelineForStreamWhenNotPausedWithRealTime() throws Exception {
        interactor.incrementCalls();

        interactor.refreshStreamTimeline(ID_STREAM, 0L, NOT_PAUSED, spyCallback, errorCallback);

        verify(shootrTimelineService).refreshTimelinesForStream(ID_STREAM, IS_REAL_TIME);
    }

    @Test public void shouldRefreshTimelineForStreamWhenPausedWithRealTime() throws Exception {
        interactor.incrementCalls();

        interactor.refreshStreamTimeline(ID_STREAM, new Date().getTime(), PAUSED, spyCallback, errorCallback);

        verify(shootrTimelineService).refreshTimelinesForStream(ID_STREAM, IS_REAL_TIME);
    }

    private Timeline timelineForStream() {
        Timeline timeline = new Timeline();
        timeline.setShots(new ArrayList<Shot>());
        return timeline;
    }

    static class SpyCallback implements Interactor.Callback<Timeline> {

        public List<Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }
}