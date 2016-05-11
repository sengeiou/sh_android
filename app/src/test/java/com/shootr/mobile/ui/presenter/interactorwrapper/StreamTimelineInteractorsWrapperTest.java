package com.shootr.mobile.ui.presenter.interactorwrapper;

import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.GetOlderStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.GetOlderViewOnlyStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.GetStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.GetViewOnlyStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.RefreshStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.RefreshViewOnlyStreamTimelineInteractor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

public class StreamTimelineInteractorsWrapperTest {

  public static final String ID_STREAM = "idStream";
  public static final long LAST_REFRESH_DATE = 0L;
  private StreamTimelineInteractorsWrapper streamTimelineInteractorsWrapper;
  @Mock RefreshStreamTimelineInteractor refreshStreamTimelineInteractor;
  @Mock RefreshViewOnlyStreamTimelineInteractor refreshViewOnlyStreamTimelineInteractor;
  @Mock GetStreamTimelineInteractor getStreamTimelineInteractor;
  @Mock GetViewOnlyStreamTimelineInteractor getViewOnlyStreamTimelineInteractor;
  @Mock GetOlderStreamTimelineInteractor getOlderStreamTimelineInteractor;
  @Mock GetOlderViewOnlyStreamTimelineInteractor getOlderViewOnlyStreamTimelineInteractor;
  @Mock Interactor.Callback<Timeline> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    streamTimelineInteractorsWrapper =
        new StreamTimelineInteractorsWrapper(refreshStreamTimelineInteractor,
            refreshViewOnlyStreamTimelineInteractor, getStreamTimelineInteractor,
            getViewOnlyStreamTimelineInteractor, getOlderStreamTimelineInteractor,
            getOlderViewOnlyStreamTimelineInteractor);
  }

  @Test public void shouldGetStreamTimelineWhenStreamModeIsZero() throws Exception {
    streamTimelineInteractorsWrapper.loadTimeline(ID_STREAM, false, 0, callback);

    verify(getStreamTimelineInteractor).loadStreamTimeline(anyString(), anyBoolean(),
        anyCallback());
  }

  @Test public void shouldGetViewOnlyStreamTimelineWhenStreamModeIsOne() throws Exception {
    streamTimelineInteractorsWrapper.loadTimeline(ID_STREAM, false, 1, callback);

    verify(getViewOnlyStreamTimelineInteractor).loadStreamTimeline(anyString(), anyBoolean(),
        anyCallback());
  }

  @Test public void shouldrefreshStreamTimelineWhenStreamModeIsZero() throws Exception {
    streamTimelineInteractorsWrapper.refreshTimeline(ID_STREAM, LAST_REFRESH_DATE, false, 0,
        callback, errorCallback);

    verify(refreshStreamTimelineInteractor).refreshStreamTimeline(anyString(), anyLong(),
        anyBoolean(), anyCallback(), anyErrorCallback());
  }

  @Test public void shouldRefreshViewOnlyStreamTimelineWhenStreamModeIsOne() throws Exception {
    streamTimelineInteractorsWrapper.refreshTimeline(ID_STREAM, LAST_REFRESH_DATE, false, 1,
        callback, errorCallback);

    verify(refreshViewOnlyStreamTimelineInteractor).refreshStreamTimeline(anyString(), anyLong(),
        anyBoolean(), anyCallback(), anyErrorCallback());
  }

  @Test public void shouldGetOlderStreamTimelineInteractorWhenStreamModeIsZero() throws Exception {
    streamTimelineInteractorsWrapper.obtainOlderTimeline(LAST_REFRESH_DATE, 0, callback,
        errorCallback);

    verify(getOlderStreamTimelineInteractor).loadOlderStreamTimeline(anyLong(), anyCallback(),
        anyErrorCallback());
  }

  @Test public void shouldGetOlderViewOnlyStreamTimelineWhenStreamModeIsOne() throws Exception {
    streamTimelineInteractorsWrapper.obtainOlderTimeline(LAST_REFRESH_DATE, 1, callback,
        errorCallback);

    verify(getOlderViewOnlyStreamTimelineInteractor).loadOlderStreamTimeline(anyLong(),
        anyCallback(), anyErrorCallback());
  }

  protected Interactor.ErrorCallback anyErrorCallback() {
    return any(Interactor.ErrorCallback.class);
  }

  protected Interactor.Callback anyCallback() {
    return any(Interactor.Callback.class);
  }
}
