package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
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

    public static final String ID_WATCHING_STREAM = "idWatchingStream";
    private RefreshActivityTimelineInteractor interactor;

    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock com.shootr.mobile.domain.service.shot.ShootrTimelineService shootrTimelineService;
    @Mock com.shootr.mobile.domain.repository.SessionRepository sessionRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.interactor = new RefreshActivityTimelineInteractor(interactorHandler, postExecutionThread,
                shootrTimelineService, sessionRepository);
    }

    @Test
    public void shouldCallbackActivityTimelineWhenServiceReturnsTimelineForActivity() throws Exception {
        when(shootrTimelineService.refreshTimelinesForActivity()).thenReturn(timelineForActivity());
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        interactor.refreshActivityTimeline(spyCallback, errorCallback);

        verify(spyCallback).onLoaded(timelineForActivity());
    }

    private User user() {
        User user = new User();
        user.setIdWatchingStream(ID_WATCHING_STREAM);
        return user;
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