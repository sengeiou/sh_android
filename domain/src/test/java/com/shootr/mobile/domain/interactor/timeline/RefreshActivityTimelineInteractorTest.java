package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshActivityTimelineInteractorTest {

    public static final String ID_WATCHING_STREAM = "idWatchingStream";
    public static final String ID_USER = "idUser";
    private static final Boolean NOT_PAUSED = false;
    @Spy SpyCallback spyCallback = new SpyCallback();
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock ShootrTimelineService shootrTimelineService;
    @Mock SessionRepository sessionRepository;
    @Mock LocaleProvider localeProvider;
    @Mock UserRepository localUserRepository;
    private RefreshActivityTimelineInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.interactor = new RefreshActivityTimelineInteractor(interactorHandler,
          postExecutionThread,
          shootrTimelineService,
          sessionRepository, localUserRepository);
    }

    @Test public void shouldCallbackActivityTimelineWhenServiceReturnsTimelineForActivity() throws Exception {
        when(shootrTimelineService.refreshTimelinesForActivity(anyString())).thenReturn(timelineForActivity());
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        interactor.refreshActivityTimeline(isUserActivityTimeline, anyString(), spyCallback, errorCallback);

        verify(spyCallback).onLoaded(timelineForActivity());
    }

    @Test public void shouldRefreshTimelinesForStreamWhenLocalRepositoryReturnsUserWithWatchingStream() throws Exception {
        when(shootrTimelineService.refreshTimelinesForActivity(anyString())).thenReturn(timelineForActivity());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(user());

        interactor.refreshActivityTimeline(isUserActivityTimeline, anyString(), spyCallback, errorCallback);

        verify(shootrTimelineService).refreshTimelinesForStream(anyString(), anyBoolean());
    }

    @Test public void shouldNotRefreshTimelinesForStreamWhenLocalRepositoryReturnsUserWithoutWatchingStream() throws Exception {
        when(shootrTimelineService.refreshTimelinesForActivity(anyString())).thenReturn(timelineForActivity());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(userWithoutWatchingStream());

        interactor.refreshActivityTimeline(isUserActivityTimeline, anyString(), spyCallback, errorCallback);

        verify(shootrTimelineService, never()).refreshTimelinesForStream(anyString(), anyBoolean());
    }

    @Test public void shouldNotRefreshTimelinesForStreamWhenLocalRepositoryReturnsNullUser() throws Exception {
        when(shootrTimelineService.refreshTimelinesForActivity(anyString())).thenReturn(timelineForActivity());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getUserById(ID_USER)).thenReturn(null);

        interactor.refreshActivityTimeline(isUserActivityTimeline, anyString(), spyCallback, errorCallback);

        verify(shootrTimelineService, never()).refreshTimelinesForStream(anyString(), anyBoolean());
    }

    private User user() {
        User user = new User();
        user.setIdWatchingStream(ID_WATCHING_STREAM);
        return user;
    }

    private User userWithoutWatchingStream() {
        User user = new User();
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