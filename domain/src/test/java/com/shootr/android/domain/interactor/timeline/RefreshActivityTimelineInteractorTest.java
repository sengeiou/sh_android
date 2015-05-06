package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.service.shot.ShootrTimelineService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshActivityTimelineInteractorTest {

    private static final String EVENT_ID = "event";
    private static final String EVENT_AUTHOR = "author";

    private RefreshActivityTimelineInteractor interactor;

    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock ShootrTimelineService shootrTimelineService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.interactor = new RefreshActivityTimelineInteractor(interactorHandler, postExecutionThread,
                shootrTimelineService);
    }

    @Test
    public void shouldCallbackActivityTimelineWhenServiceReturnsTimelineForActivity() throws Exception {
        when(shootrTimelineService.refreshTimelines()).thenReturn(Arrays.asList(timelineForEvent(), timelineForActivity()));

        interactor.refreshActivityTimeline(spyCallback, errorCallback);

        verify(spyCallback).onLoaded(timelineForActivity());
    }

    @Test
    public void shouldCallbackErrorWhenServiceDontReturnTimelineForActivity() throws Exception {
        when(shootrTimelineService.refreshTimelines()).thenReturn(Collections.singletonList(timelineForEvent()));

        interactor.refreshActivityTimeline(spyCallback, errorCallback);

        verify(errorCallback).onError(any(ShootrException.class));
    }

    @Test
    public void shouldNotCallbackResultWhenServiceDontReturnsTimelineForActivity() throws Exception {
        when(shootrTimelineService.refreshTimelines()).thenReturn(Collections.singletonList(timelineForEvent()));

        interactor.refreshActivityTimeline(spyCallback, errorCallback);

        verify(spyCallback, never()).onLoaded(any(Timeline.class));
    }

    private Timeline timelineForEvent() {
        Timeline timeline = new Timeline();
        timeline.setShots(new ArrayList<Shot>());
        timeline.setParameters(TimelineParameters.builder().forEvent(EVENT_ID, EVENT_AUTHOR).forUsers(dummyThreeUserList()).build());
        return timeline;
    }

    private Timeline timelineForActivity() {
        Timeline timeline = new Timeline();
        timeline.setShots(new ArrayList<Shot>());
        timeline.setParameters(TimelineParameters.builder().forActivity().forUsers(dummyThreeUserList()).build());
        return timeline;
    }

    private List<String> dummyThreeUserList() {
        return Arrays.asList("a", "b", "c");
    }

    static class SpyCallback implements Interactor.Callback<Timeline> {

        public List<Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }

}