package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.service.shot.ShootrTimelineService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshStreamTimelineInteractorTest {

    private RefreshEventTimelineInteractor interactor;

    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock ShootrTimelineService shootrTimelineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.interactor = new RefreshEventTimelineInteractor(interactorHandler, postExecutionThread,
                shootrTimelineService);
    }

    @Test
    public void shouldCallbackEventTimelineWhenServiceReturnsTimelineForEvent() throws Exception {
        when(shootrTimelineService.refreshTimelinesForWatchingEvent()).thenReturn(timelineForEvent());

        interactor.refreshEventTimeline(spyCallback, errorCallback);

        verify(spyCallback).onLoaded(timelineForEvent());
    }

    private Timeline timelineForEvent() {
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