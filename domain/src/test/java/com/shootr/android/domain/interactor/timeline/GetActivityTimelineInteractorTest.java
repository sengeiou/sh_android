package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.SpyCallback;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class GetActivityTimelineInteractorTest {

    private static final String WATCHING_EVENT_ID = "watching_event";
    private static final String EVENT_AUTHOR_ID = "event_author";
    private static final String ID_CURRENT_USER = "current_user";

    private static final Long DATE_OLDER = 1000L;
    private static final Long DATE_MIDDLE = 2000L;
    private static final Long DATE_NEWER = 3000L;

    @Mock ActivityRepository localActivityRepository;
    @Mock UserRepository localUserRepository;
    @Spy com.shootr.android.domain.interactor.SpyCallback<ActivityTimeline> spyCallback = new com.shootr.android.domain.interactor.SpyCallback<>();
    @Mock EventRepository eventRepository;
    @Mock SessionRepository sessionRepository;
    @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;
    @Mock Interactor.ErrorCallback errorCallback;

    private GetActivityTimelineInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        when(localUserRepository.getPeople()).thenReturn(people());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);

        interactor = new GetActivityTimelineInteractor(interactorHandler,
                postExecutionThread,
                sessionRepository, localActivityRepository,
                localUserRepository
        );
    }

    @Test
    public void shouldCallbackShotsInOrderWithPublishDateComparator() throws Exception {
        setupWatchingEvent();
        when(localActivityRepository.getActivityTimeline(any(ActivityTimelineParameters.class))).thenReturn(
          unorderedActivities());

        interactor.loadActivityTimeline(spyCallback, errorCallback);
        List<Activity> localShotsReturned = spyCallback.lastResult().getActivities();

        assertThat(localShotsReturned).isSortedAccordingTo(new Activity.NewerAboveComparator());
    }

    @Test
    public void shouldCallbackShotsInOrderWithPublishDateComparatorWithNoEventWatching() throws Exception {
        when(localActivityRepository.getActivityTimeline(any(ActivityTimelineParameters.class))).thenReturn(
          unorderedActivities());

        interactor.loadActivityTimeline(spyCallback, errorCallback);
        List<Activity> localShotsReturned = spyCallback.lastResult().getActivities();

        assertThat(localShotsReturned).isSortedAccordingTo(new Activity.NewerAboveComparator());
    }

    private User currentUserWatching() {
        User user = new User();
        user.setIdUser(ID_CURRENT_USER);
        user.setIdWatchingEvent(WATCHING_EVENT_ID);
        return user;
    }

    private List<Activity> unorderedActivities() {
        return Arrays.asList(activityWithDate(DATE_MIDDLE), activityWithDate(DATE_OLDER), activityWithDate(DATE_NEWER));
    }

    private Activity activityWithDate(Long date) {
        Activity activity = new Activity();
        activity.setPublishDate(new Date(date));
        return activity;
    }

    private void setupWatchingEvent() {
        when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserWatching());
        when(eventRepository.getEventById(eq(WATCHING_EVENT_ID))).thenReturn(watchingEvent());
    }

    //region Stubs
    private List<User> people() {
        return Arrays.asList(new User());
    }

    private Event watchingEvent() {
        Event event = new Event();
        event.setId(WATCHING_EVENT_ID);
        event.setAuthorId(EVENT_AUTHOR_ID);
        return event;
    }

    static class SpyCallback implements Interactor.Callback<Timeline> {

        public List<Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }
    //endregion
}