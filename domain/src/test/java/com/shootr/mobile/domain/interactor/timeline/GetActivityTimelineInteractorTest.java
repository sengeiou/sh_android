package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.TimelineSynchronizationRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class GetActivityTimelineInteractorTest {

    private static final String WATCHING_STREAM_ID = "watching_stream";
    private static final String STREAM_AUTHOR_ID = "stream_author";
    private static final String ID_CURRENT_USER = "current_user";

    private static final Long DATE_OLDER = 1000L;
    private static final Long DATE_MIDDLE = 2000L;
    private static final Long DATE_NEWER = 3000L;
    public static final String LANGUAGE = "LANGUAGE";
    public static final boolean NOT_USER_ACTIVITY_TIMELINE = false;
    public static final boolean IS_USER_ACTIVITY_TIMELINE = true;

    @Mock ActivityRepository localActivityRepository;
    @Mock UserRepository localUserRepository;
    @Spy com.shootr.mobile.domain.interactor.SpyCallback<ActivityTimeline> spyCallback =
      new com.shootr.mobile.domain.interactor.SpyCallback<>();
    @Mock StreamRepository streamRepository;
    @Mock SessionRepository sessionRepository;
    @Mock TimelineSynchronizationRepository timelineSynchronizationRepository;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock LocaleProvider localeProvider;

    private GetActivityTimelineInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        when(localUserRepository.getPeople()).thenReturn(people());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);

        interactor = new GetActivityTimelineInteractor(interactorHandler,
          postExecutionThread,
          localActivityRepository,
          sessionRepository);
    }

    @Test public void shouldCallbackShotsInOrderWithPublishDateComparator() throws Exception {
        setupWatchingStream();
        when(localActivityRepository.getActivityTimeline(any(ActivityTimelineParameters.class),
          anyString())).thenReturn(unorderedActivities());

        interactor.loadActivityTimeline(NOT_USER_ACTIVITY_TIMELINE, localeProvider.getLanguage(), spyCallback);
        List<Activity> localShotsReturned = spyCallback.lastResult().getActivities();

        assertThat(localShotsReturned).isSortedAccordingTo(new Activity.NewerAboveComparator());
    }

    @Test public void shouldCallbackShotsInOrderWithPublishDateComparatorWithNoStreamWatching() throws Exception {
        when(localActivityRepository.getActivityTimeline(any(ActivityTimelineParameters.class),
          anyString())).thenReturn(unorderedActivities());

        interactor.loadActivityTimeline(NOT_USER_ACTIVITY_TIMELINE, localeProvider.getLanguage(), spyCallback);
        List<Activity> localShotsReturned = spyCallback.lastResult().getActivities();

        assertThat(localShotsReturned).isSortedAccordingTo(new Activity.NewerAboveComparator());
    }

    @Test public void shouldCallbackShotsInOrderWithPublishDateComparatorAndIsUserTimeline() throws Exception {
        setupWatchingStream();
        when(localActivityRepository.getActivityTimeline(any(ActivityTimelineParameters.class),
          anyString())).thenReturn(unorderedActivities());

        interactor.loadActivityTimeline(IS_USER_ACTIVITY_TIMELINE, localeProvider.getLanguage(), spyCallback);
        List<Activity> localShotsReturned = spyCallback.lastResult().getActivities();

        assertThat(localShotsReturned).isSortedAccordingTo(new Activity.NewerAboveComparator());
    }

    @Test public void shouldCallbackShotsInOrderWithPublishDateComparatorWithNoStreamWatchingAndIsUserTimeline()
      throws Exception {
        when(localActivityRepository.getActivityTimeline(any(ActivityTimelineParameters.class),
          anyString())).thenReturn(unorderedActivities());

        interactor.loadActivityTimeline(IS_USER_ACTIVITY_TIMELINE, localeProvider.getLanguage(), spyCallback);
        List<Activity> localShotsReturned = spyCallback.lastResult().getActivities();

        assertThat(localShotsReturned).isSortedAccordingTo(new Activity.NewerAboveComparator());
    }

    private User currentUserWatching() {
        User user = new User();
        user.setIdUser(ID_CURRENT_USER);
        user.setIdWatchingStream(WATCHING_STREAM_ID);
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

    private void setupWatchingStream() {
        when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUserWatching());
        when(streamRepository.getStreamById(eq(WATCHING_STREAM_ID))).thenReturn(watchingStream());
    }

    //region Stubs
    private List<User> people() {
        return Arrays.asList(new User());
    }

    private Stream watchingStream() {
        Stream stream = new Stream();
        stream.setId(WATCHING_STREAM_ID);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        return stream;
    }

    static class SpyCallback implements Interactor.Callback<Timeline> {

        public List<Timeline> timelinesReturned = new ArrayList<>();

        @Override public void onLoaded(Timeline timeline) {
            timelinesReturned.add(timeline);
        }
    }
    //endregion
}