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
    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock ShootrTimelineService shootrTimelineService;
    @Mock LocaleProvider localeProvider;
    private RefreshStreamTimelineInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.interactor =
          new RefreshStreamTimelineInteractor(interactorHandler, postExecutionThread, shootrTimelineService,
            localeProvider);
    }

    @Test public void shouldCallbackStreamTimelineWhenServiceReturnsTimelineForStream() throws Exception {
        when(shootrTimelineService.refreshTimelinesForStream(ID_STREAM)).thenReturn(timelineForStream());

        interactor.refreshStreamTimeline(ID_STREAM, spyCallback, errorCallback);

        verify(spyCallback).onLoaded(timelineForStream());
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