package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetOlderActivityTimelineInteractorTest {
  public static final boolean NOT_USER_ACTIVITY_TIMELINE = false;
  public static final boolean IS_USER_ACTIVITY_TIMELINE = true;
  public static final long CURRENT_OLDEST_DATE = 0L;
  public static final String LANGUAGE = "language";
  private static final Long DATE_OLDER = 1000L;
  private static final Long DATE_MIDDLE = 2000L;
  private static final Long DATE_NEWER = 3000L;
  public static final String ID_USER = "id_user";
  public static final String ANOTHER_ID_USER = "another_id_user";
  private GetOlderActivityTimelineInteractor getOlderActivityTimelineInteractor;
  @Mock ActivityRepository remoteActivityRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Interactor.Callback<ActivityTimeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Captor ArgumentCaptor<ActivityTimeline> activityTimelineArgumentCaptor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getOlderActivityTimelineInteractor =
        new GetOlderActivityTimelineInteractor(interactorHandler, postExecutionThread,
            remoteActivityRepository, sessionRepository);
  }

  @Test public void shouldLoadOlderActivities() throws Exception {
    getOlderActivityTimelineInteractor.loadOlderActivityTimeline(NOT_USER_ACTIVITY_TIMELINE,
        CURRENT_OLDEST_DATE, LANGUAGE, callback, errorCallback);

    verify(remoteActivityRepository).getActivityTimeline(any(ActivityTimelineParameters.class),
        anyString());
  }

  @Test public void shouldNotifyLoadedOlderActivities() throws Exception {
    getOlderActivityTimelineInteractor.loadOlderActivityTimeline(NOT_USER_ACTIVITY_TIMELINE,
        CURRENT_OLDEST_DATE, LANGUAGE, callback, errorCallback);

    verify(callback).onLoaded(any(ActivityTimeline.class));
  }

  @Test public void shouldNotifyErrorRemoteWhenActivityRepositoryThrowsError() throws Exception {
    when(remoteActivityRepository.getActivityTimeline(any(ActivityTimelineParameters.class),
        anyString())).thenThrow(new ShootrException() {
    });

    getOlderActivityTimelineInteractor.loadOlderActivityTimeline(NOT_USER_ACTIVITY_TIMELINE,
        CURRENT_OLDEST_DATE, LANGUAGE, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyOnlyUsersCallbackIfIsUserTimeline() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(remoteActivityRepository.getActivityTimeline(any(ActivityTimelineParameters.class),
        anyString())).thenReturn(unorderedActivities());

    getOlderActivityTimelineInteractor.loadOlderActivityTimeline(IS_USER_ACTIVITY_TIMELINE,
        CURRENT_OLDEST_DATE, LANGUAGE, callback, errorCallback);

    verify(callback).onLoaded(activityTimelineArgumentCaptor.capture());
    assertThat(activityTimelineArgumentCaptor.getValue().getActivities()).hasSize(2);
  }

  private List<Activity> unorderedActivities() {
    return Arrays.asList(activity(ID_USER, DATE_MIDDLE), activity(ANOTHER_ID_USER, DATE_OLDER),
        activity(ID_USER, DATE_NEWER));
  }

  private Activity activity(String idUser, Long date) {
    Activity activity = new Activity();
    activity.setIdUser(idUser);
    activity.setPublishDate(new Date(date));
    return activity;
  }
}
