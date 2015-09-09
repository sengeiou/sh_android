package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
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

public class RefreshActivityTimelineInteractorTest {

    private RefreshActivityTimelineInteractor interactor;

    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock ShootrTimelineService shootrTimelineService;
    @Mock SessionRepository sessionRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.interactor = new RefreshActivityTimelineInteractor(interactorHandler, postExecutionThread,
                shootrTimelineService, sessionRepository);
    }

    @Test
    public void shouldCallbackActivityTimelineWhenServiceReturnsTimelineForActivity() throws Exception {
        when(shootrTimelineService.refreshTimelinesForActivity()).thenReturn(timelineForActivity());

        interactor.refreshActivityTimeline(spyCallback, errorCallback);

        verify(spyCallback).onLoaded(timelineForActivity());
    }

    private ActivityTimeline timelineForActivity() {
        ActivityTimeline timeline = new ActivityTimeline();
        timeline.setActivities(new ArrayList<Activity>());
        return timeline;
    }

    static class SpyCallback implements Interactor.Callback<ActivityTimeline> {

        public List<ActivityTimeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(ActivityTimeline timeline) {
            timelinesReturned.add(timeline);
        }
    }

}