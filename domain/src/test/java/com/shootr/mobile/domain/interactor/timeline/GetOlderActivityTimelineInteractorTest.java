package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetOlderActivityTimelineInteractorTest {
  public static final boolean NOT_USER_ACTIVITY_TIMELINE = false;
  public static final long CURRENT_OLDEST_DATE = 0L;
  public static final String LANGUAGE = "language";
  public static final String ID_USER = "id_user";
  private com.shootr.mobile.domain.interactor.timeline.activity.GetOlderActivityTimelineInteractor
      getOlderActivityTimelineInteractor;
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
        new com.shootr.mobile.domain.interactor.timeline.activity.GetOlderActivityTimelineInteractor(
            interactorHandler, postExecutionThread, remoteActivityRepository);
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
}
