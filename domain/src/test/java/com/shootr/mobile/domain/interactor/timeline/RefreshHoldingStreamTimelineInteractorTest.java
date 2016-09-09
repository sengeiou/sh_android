package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RefreshHoldingStreamTimelineInteractorTest {

  public static final String STREAM_ID = "stream_id";
  public static final String USER_ID = "user_id";
  public static final long LAST_REFRESH_DATE = 0L;
  public static final boolean GONE_BACKGROUND = false;
  private RefreshHoldingStreamTimelineInteractor refreshHoldingStreamTimelineInteractor;
  @Mock ShootrTimelineService shootrTimelineService;
  @Mock LocaleProvider localeProvider;
  @Mock Interactor.Callback<Timeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    refreshHoldingStreamTimelineInteractor =
        new RefreshHoldingStreamTimelineInteractor(interactorHandler, postExecutionThread,
            shootrTimelineService, localeProvider);
  }

  @Test public void shouldRefreshHoldingTimelineForStream() throws Exception {
    refreshHoldingStreamTimelineInteractor.refreshHoldingStreamTimeline(STREAM_ID, USER_ID,
        LAST_REFRESH_DATE, GONE_BACKGROUND, callback, errorCallback);

    verify(shootrTimelineService).refreshHoldingTimelineForStream(anyString(), anyString(),
        anyBoolean());
  }

  @Test public void shouldNotifyLoadedWhenRefreshHoldingTimelineForStream() throws Exception {
    refreshHoldingStreamTimelineInteractor.refreshHoldingStreamTimeline(STREAM_ID, USER_ID,
        LAST_REFRESH_DATE, GONE_BACKGROUND, callback, errorCallback);

    verify(callback).onLoaded(any(Timeline.class));
  }

  @Test public void shouldrefreshTimelinesForActivity() throws Exception {
    refreshHoldingStreamTimelineInteractor.refreshHoldingStreamTimeline(STREAM_ID, USER_ID,
        LAST_REFRESH_DATE, GONE_BACKGROUND, callback, errorCallback);

    verify(shootrTimelineService, atLeastOnce()).refreshTimelinesForActivity(anyString(), anyBoolean());
  }

  @Test public void shouldNotifyErrorIfServiceThrowsExceptionWhenRefreshHoldingStream()
      throws Exception {
    when(shootrTimelineService.refreshHoldingTimelineForStream(anyString(), anyString(),
        anyBoolean())).thenThrow(new ShootrException() {
    });

    refreshHoldingStreamTimelineInteractor.refreshHoldingStreamTimeline(STREAM_ID, USER_ID,
        LAST_REFRESH_DATE, GONE_BACKGROUND, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyErrorIfServiceThrowsExceptionWhenRefreshTimelinesForActivity()
      throws Exception {
    when(shootrTimelineService.refreshTimelinesForActivity(anyString(), anyBoolean())).thenThrow(
        new ShootrException() {
        });

    refreshHoldingStreamTimelineInteractor.refreshHoldingStreamTimeline(STREAM_ID, USER_ID,
        LAST_REFRESH_DATE, GONE_BACKGROUND, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }
}
