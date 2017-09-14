package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.CallCtaCheckInInteractor;
import com.shootr.mobile.domain.interactor.shot.DeleteLocalShotsByStreamInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UndoReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.stream.GetNewFilteredShotsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UpdateStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.ReloadStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.UpdateWatchNumberInteractor;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.Timeline;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.StreamHoldingTimelineInteractorsWrapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.StreamTimelineInteractorsWrapper;
import com.shootr.mobile.ui.views.StreamTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StreamTimelinePresenterTest {

  private static final Date LAST_SHOT_DATE = new Date();
  private static final ShotSent.Event SHOT_SENT_EVENT = null;
  private static final String SELECTED_STREAM_ID = "stream";
  public static final String ID_STREAM = "ID_STREAM";
  public static final String ID_AUTHOR = "idAuthor";
  private static final Integer OLD_LIST_SIZE = 10;
  public static final Integer NEW_SHOTS_NUMBER = 10;
  private static final Integer ZERO_NEW_SHOTS = 0;
  private static final String TOPIC = "topic";
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String EMPTY_TOPIC = "";
  private static final boolean NOTIFY = true;
  private static final String USER_ID = "userId";
  private static final String OTHER_USER_ID = "otherId";
  private static final int PUBLIC = 0;
  private static final int VIEW_ONLY = 1;

  @Mock StreamTimelineView streamTimelineView;
  @Mock StreamTimelineInteractorsWrapper timelineInteractorWrapper;
  @Mock SelectStreamInteractor selectStreamInteractor;
  @Mock MarkNiceShotInteractor markNiceShotInteractor;
  @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  @Mock ReshootInteractor reshootInteractor;
  @Mock UndoReshootInteractor undoReshootInteractor;
  @Mock Bus bus;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock Poller poller;
  @Mock StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper;
  @Mock DeleteLocalShotsByStreamInteractor deleteLocalShotsByStreamInteractor;
  @Mock ReloadStreamTimelineInteractor reloadStreamTimelineInteractor;
  @Mock GetStreamInteractor getStreamInteractor;
  @Mock UpdateWatchNumberInteractor updateWatchNumberInteractor;
  @Captor ArgumentCaptor<Boolean> booleanArgumentCaptor;
  @Mock SessionRepository sessionRepository;
  @Mock CallCtaCheckInInteractor callCtaCheckInInteractor;
  @Mock GetNewFilteredShotsInteractor getNewFilteredShotsInteractor;
  @Mock UpdateStreamInteractor updateStreamInteractor;
  private StreamTimelinePresenter presenter;
  private ShotSent.Receiver shotSentReceiver;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ShotModelMapper shotModelMapper = new ShotModelMapper(sessionRepository);
    StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
    presenter = new StreamTimelinePresenter(timelineInteractorWrapper,
        streamHoldingTimelineInteractorsWrapper, selectStreamInteractor, markNiceShotInteractor,
        unmarkNiceShotInteractor, callCtaCheckInInteractor, reshootInteractor,
        undoReshootInteractor, shotModelMapper,
        streamModelMapper, bus, errorMessageFactory, poller,
        updateWatchNumberInteractor,
        updateStreamInteractor, getNewFilteredShotsInteractor, sessionRepository);
    presenter.setView(streamTimelineView);
    shotSentReceiver = presenter;
  }

  //region Select stream

  @Test public void shouldSelectStreamWhenInitialized() throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());

    presenter.initialize(streamTimelineView, ID_STREAM, SELECTED_STREAM_ID, PUBLIC);

    verify(selectStreamInteractor).selectStream(eq(ID_STREAM), anySelectCallback(),
        anyErrorCallback());
  }
  //endregion

  //region Load timeline
  @Test public void shouldLoadTimlelineWhenSelectStreamIfSelectStreamCallbacks() throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    presenter.selectStream();

    verify(timelineInteractorWrapper).loadTimeline(anyString(), anyBoolean(), anyBoolean(), anyInt(),
        anyCallback());
  }

  @Test public void shouldRenderTimelineShotsInViewWhenLoadTimelineRespondsShotsAndIsFirstPosition()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView).addShots(anyListOf(ShotModel.class));
  }

  @Test
  public void shouldRenderTimelineShotsInViewWhenLoadTimelineRespondsShotsAndIsFirstPositionAndIsViewOnly()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView).addShots(anyListOf(ShotModel.class));
  }

  @Test public void shouldAddAboveInViewWhenLoadTimelineRespondsShotsAndIsNotFirstPosition()
      throws Exception {
    setupShotsModels();
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupOldListSize();
    setupNewShotsNumbers();

    presenter.setStreamMode(PUBLIC);
    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView).addAbove(anyListOf(ShotModel.class));
  }

  @Test
  public void shouldAddAboveInViewWhenLoadTimelineRespondsShotsAndIsNotFirstPositionAndIsViewOnlyStream()
      throws Exception {
    setupShotsModels();
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupIsNotFirstLoad();
    setupOldListSize();
    setupNewShotsNumbers();

    presenter.setStreamMode(0);
    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView).addAbove(anyListOf(ShotModel.class));
  }

  @Test public void shouldShowShotsInViewWhenLoadTimelineRespondsShotsAndFirstLoad()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsFirstLoad();
    setupShotsModels();

    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView).showShots();
  }

  @Test
  public void shouldShowShotsInViewWhenLoadTimelineRespondsShotsAndFirstLoadAndIsViewOnlyStream()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsFirstLoad();
    setupShotsModels();

    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView).showShots();
  }

  @Test public void shouldShowShotsInViewWhenLoadTimelineRespondsShotsAndIsNotFirstPosition()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupOldListSize();
    setupNewShotsNumbers();
    setupShotsModels();

    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView).showShots();
  }

  @Test
  public void shouldShowShotsInViewWhenLoadTimelineRespondsShotsAndIsNotFirstPositionAndIsViewOnlyStream()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupOldListSize();
    setupNewShotsNumbers();
    setupShotsModels();

    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView).showShots();
  }

  @Test public void shouldNotShowLoadingViewWhenLoadTimeline() throws Exception {
    presenter.setStreamMode(PUBLIC);

    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView, never()).showLoading();
  }

  @Test public void shouldNotShowLoadingViewWhenLoadTimelineAndIsViewOnly() throws Exception {
    presenter.setStreamMode(VIEW_ONLY);

    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView, never()).showLoading();
  }

  @Test public void shouldNotHideLoadingViewWhenLoadTimelineRespondsShots() throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView, never()).hideLoading();
  }

  @Test public void shouldNotHideLoadingViewWhenLoadTimelineRespondsShotsAndIsViewOnlyStream()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView, never()).hideLoading();
  }

  @Test public void shouldNotHideLoadingViewWhenLoadTimelineRespondsEmptyShotList()
      throws Exception {
    presenter.setStreamMode(PUBLIC);
    setupLoadTimelineInteractorCallbacks(emptyTimeline());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView, never()).hideLoading();
  }

  @Test public void shouldNotHideLoadingViewWhenLoadTimelineRespondsEmptyShotListAndIsViewOnly()
      throws Exception {
    presenter.setStreamMode(VIEW_ONLY);
    setupLoadTimelineInteractorCallbacks(emptyTimeline());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView, never()).hideLoading();
  }

  @Test public void shouldShowEmptyViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
    setupLoadTimelineInteractorCallbacks(emptyTimeline());
    setupIsFirstLoad();
    setupShotsModels();

    presenter.setStreamMode(PUBLIC);
    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView).showEmpty();
  }

  @Test public void shouldShowEmptyViewWhenLoadTimelineRespondsEmptyShotListAndIsViewOnly()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(emptyTimeline());
    setupIsFirstLoad();
    setupShotsModels();

    presenter.setStreamMode(VIEW_ONLY);
    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView).showEmpty();
  }

  @Test public void shouldHideEmptyViewWhenLoadTimelineRespondsShots() throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.setIsFirstLoad(true);
    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView).hideEmpty();
  }

  @Test public void shouldHideEmptyViewWhenLoadTimelineRespondsShotsAndIsViewOnlyStream()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.setIsFirstLoad(true);
    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView).hideEmpty();
  }

  @Test public void shouldHideEmtpyViewWhenLoadTimelineRespondsShots() throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.setIsFirstLoad(true);
    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView).hideEmpty();
  }

  @Test public void shouldHideEmtpyViewWhenLoadTimelineRespondsShotsAndIsViewOnlyStream()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupShotsModels();

    presenter.setIsFirstLoad(true);
    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView).hideEmpty();
  }

  @Test public void shouldHideTimelineShotsWhenGetMainTimelineRespondsEmptyShotList()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(emptyTimeline());
    setupIsFirstLoad();
    setupShotsModels();

    presenter.loadTimeline(PUBLIC);

    verify(streamTimelineView, atLeastOnce()).hideShots();
  }

  @Test
  public void shouldHideTimelineShotsWhenGetMainTimelineRespondsEmptyShotListAndIsViewOnlyStream()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(emptyTimeline());
    setupIsFirstLoad();
    setupShotsModels();

    presenter.loadTimeline(VIEW_ONLY);

    verify(streamTimelineView, atLeastOnce()).hideShots();
  }

  @Test public void shouldRenderEmtpyShotListWhenGetMainTimelineRespondsEmptyShotList()
      throws Exception {
    setupLoadTimelineInteractorCallbacks(emptyTimeline());
    setupIsFirstLoad();
    setupShotsModels();

    presenter.loadTimeline(PUBLIC);

    ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
    verify(streamTimelineView).setShots(captor.capture());
    List renderedShotList = captor.<List<ShotModel>>getValue();
    assertThat(renderedShotList).isEmpty();
  }
  //endregion

  //region Refresh main timeline
  @Test public void shouldSAddShotsWhenRefresh() throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstLoadAndIsFirstPosition();

    presenter.initialize(streamTimelineView, ID_STREAM, null, PUBLIC);
    presenter.refresh();

    verify(streamTimelineView).addShots(anyListOf(ShotModel.class));
  }

  @Test public void shouldSAddShotsWhenRefreshAndIsViewOnlyStream() throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstLoadAndIsFirstPosition();
    presenter.setStreamMode(VIEW_ONLY);

    presenter.initialize(streamTimelineView, ID_STREAM, null, VIEW_ONLY);
    presenter.refresh();

    verify(streamTimelineView).addShots(anyListOf(ShotModel.class));
  }

  @Test public void shouldSetShotsWhenRefreshTimelineRespondsShotsAndIsNotFirstPosition()
      throws Exception {
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupOldListSize();
    setupNewShotsNumbers();
    setupShotsModels();
    presenter.setStreamMode(PUBLIC);

    presenter.refresh();

    verify(streamTimelineView, atLeastOnce()).addAbove(anyListOf(ShotModel.class));
  }

  @Test
  public void shouldSetShotsWhenRefreshTimelineRespondsShotsAndIsNotFirstPositionAndIsViewOnly()
      throws Exception {
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupOldListSize();
    setupNewShotsNumbers();
    setupShotsModels();
    presenter.setStreamMode(VIEW_ONLY);

    presenter.refresh();

    verify(streamTimelineView, atLeastOnce()).addAbove(anyListOf(ShotModel.class));
  }

  @Test public void shouldNotShowLoadingWhenRefreshTimeline() throws Exception {
    presenter.refresh();

    verify(streamTimelineView, never()).showLoading();
  }

  @Test public void shouldNotShowLoadingWhenRefreshTimelineAndIsViewOnlyStream() throws Exception {
    presenter.setStreamMode(VIEW_ONLY);
    presenter.refresh();

    verify(streamTimelineView, never()).showLoading();
  }

  @Test public void shouldHideLoadingWhenRefreshTimelineRespondsShots() throws Exception {
    presenter.setStreamMode(PUBLIC);
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());

    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);
    presenter.refresh();

    verify(streamTimelineView).hideLoading();
  }

  @Test public void shouldHideLoadingWhenRefreshTimelineRespondsShotsAndIsViewOnlyStream()
      throws Exception {
    presenter.setStreamMode(VIEW_ONLY);
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());

    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);
    presenter.refresh();

    verify(streamTimelineView).hideLoading();
  }

  @Test public void shouldHideLoadingWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
    setupRefreshTimelineInteractorCallbacks(emptyTimeline());

    presenter.refresh();

    verify(streamTimelineView).hideLoading();
  }

  @Test public void shouldHideLoadingWhenRefreshTimelineRespondsEmptyShotListAndIsViewOnlyStream()
      throws Exception {
    setupRefreshTimelineInteractorCallbacks(emptyTimeline());
    presenter.setStreamMode(VIEW_ONLY);

    presenter.refresh();

    verify(streamTimelineView).hideLoading();
  }

  @Test public void shouldShowShotsIfReceivedShotsWhenRefresTimelineAndIsNotInFirstPosition()
      throws Exception {
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupOldListSize();
    setupNewShotsNumbers();
    setupShotsModels();
    presenter.setStreamMode(PUBLIC);

    presenter.refresh();

    verify(streamTimelineView).showShots();
  }

  @Test
  public void shouldShowShotsIfReceivedShotsWhenRefresTimelineAndIsNotInFirstPositionAndViewOnly()
      throws Exception {
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupOldListSize();
    setupNewShotsNumbers();
    setupShotsModels();
    presenter.setStreamMode(VIEW_ONLY);

    presenter.refresh();

    verify(streamTimelineView).showShots();
  }

  @Test public void shouldNotShowStreamTimelineIndicatorWhenRefreshTimelineAndIsInFirstPosition()
      throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_AUTHOR);
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupFirstShotPosition();
    setupOldListSize();
    setupNewShotsNumbers();

    presenter.initialize(streamTimelineView, ID_STREAM, null, PUBLIC);
    presenter.refresh();

    verify(streamTimelineView, never()).showNewShotsIndicator(anyInt());
  }

  @Test public void shouldNotShowStreamTimelineIndicatorWhenRefreshTimelineAndNumberNewShotsIsZero()
      throws Exception {
    presenter.setStreamMode(0);
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupIsNotFirstShotPosition();
    setupOldListSize();
    setupZeroNewShotsNumbers();
    setupShotsModels();

    presenter.refresh();

    verify(streamTimelineView, never()).showNewShotsIndicator(ZERO_NEW_SHOTS);
  }

  //endregion

  //region Older shots
  @Test public void shouldObtainOlderTimelineWhenShowingLastShot() throws Exception {
    presenter.setStreamMode(PUBLIC);

    presenter.showingLastShot(lastShotModel());

    verify(timelineInteractorWrapper).obtainOlderTimeline(anyString(), anyBoolean(), anyLong(), anyInt(), anyCallback(),
        anyErrorCallback());
  }

  @Test public void shouldObtainOlderTimelineWhenShowingLastShotAndIsViewOnlyStream()
      throws Exception {
    presenter.setStreamMode(VIEW_ONLY);

    presenter.showingLastShot(lastShotModel());

    verify(timelineInteractorWrapper).obtainOlderTimeline(anyString(), anyBoolean(), anyLong(), anyInt(), anyCallback(),
        anyErrorCallback());
  }

  @Test public void shouldObtainOlderTimelineOnceWhenShowingLastShotTwiceWithoutCallbackExecuted()
      throws Exception {
    presenter.setStreamMode(PUBLIC);
    presenter.showingLastShot(lastShotModel());
    presenter.showingLastShot(lastShotModel());

    verify(timelineInteractorWrapper, times(1)).obtainOlderTimeline(anyString(), anyBoolean(), anyLong(), anyInt(),
        anyCallback(), anyErrorCallback());
  }

  @Test
  public void shouldObtainOlderTimelineOnceWhenShowingLastShotTwiceWithoutCallbackExecutedAndViewOnlyStream()
      throws Exception {
    presenter.setStreamMode(VIEW_ONLY);
    presenter.showingLastShot(lastShotModel());
    presenter.showingLastShot(lastShotModel());

    verify(timelineInteractorWrapper, times(1)).obtainOlderTimeline(anyString(), anyBoolean(), anyLong(), anyInt(),
        anyCallback(), anyErrorCallback());
  }

  @Test public void shouldShowLoadingOlderShotsWhenShowingLastShot() throws Exception {
    presenter.setStreamMode(PUBLIC);

    presenter.showingLastShot(lastShotModel());

    verify(streamTimelineView).showLoadingOldShots();
  }

  @Test public void shouldShowLoadingOlderShotsWhenShowingLastShotAndIsViewOnly() throws Exception {
    presenter.setStreamMode(VIEW_ONLY);

    presenter.showingLastShot(lastShotModel());

    verify(streamTimelineView).showLoadingOldShots();
  }

  @Test public void shouldObtainOlderTimelineOnlyOnceWhenCallbacksEmptyList() throws Exception {
    setupGetOlderTimelineInteractorCallbacks(emptyTimeline());
    presenter.setStreamMode(PUBLIC);

    presenter.showingLastShot(lastShotModel());
    presenter.showingLastShot(lastShotModel());

    verify(timelineInteractorWrapper, times(1)).obtainOlderTimeline(anyString(), anyBoolean(), anyLong(), anyInt(),
        anyCallback(), anyErrorCallback());
  }

  @Test public void shouldObtainOlderTimelineOnlyOnceWhenCallbacksEmptyListAndIsViewOnlyStream()
      throws Exception {
    setupGetOlderTimelineInteractorCallbacks(emptyTimeline());
    presenter.setStreamMode(VIEW_ONLY);

    presenter.showingLastShot(lastShotModel());
    presenter.showingLastShot(lastShotModel());

    verify(timelineInteractorWrapper, times(1)).obtainOlderTimeline(anyString(), anyBoolean(), anyLong(), anyInt(),
        anyCallback(), anyErrorCallback());
  }

  //endregion

  //region Bus streams
  @Test public void shouldRefreshTimelineWhenShotSent() throws Exception {
    shotSentReceiver.onShotSent(SHOT_SENT_EVENT);

    verify(timelineInteractorWrapper).refreshTimeline(anyString(), anyBoolean(), anyLong(), anyBoolean(),
        anyInt(), anyCallback(), anyErrorCallback());
  }

  @Test public void shouldRefreshTimelineWhenPaused() throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    presenter.setNewShotsNumber(ZERO_NEW_SHOTS);
    presenter.pause();

    presenter.resume();

    verify(timelineInteractorWrapper).loadTimeline(anyString(), anyBoolean(), anyBoolean(), anyInt(),
        anyCallback());
  }

  @Test public void shouldRefreshTimelineWhenPausedAndIsViewOnlyStream() throws Exception {
    setupSelectStreamInteractorCallbacksStream();
    presenter.setStreamMode(VIEW_ONLY);
    presenter.setNewShotsNumber(ZERO_NEW_SHOTS);
    presenter.pause();

    presenter.resume();

    verify(timelineInteractorWrapper).loadTimeline(anyString(), anyBoolean(), anyBoolean(), anyInt(),
        anyCallback());
  }

  @Test public void shouldShotSentReceiverHaveSubscribeAnnotation() throws Exception {
    String receiverMethodName = ShotSent.Receiver.class.getDeclaredMethods()[0].getName();

    Method receiverDeclaredMethod =
        shotSentReceiver.getClass().getMethod(receiverMethodName, ShotSent.Event.class);
    boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
    assertThat(annotationPresent).isTrue();
  }

  @Test public void shouldHideHolingShotsButtonWhenItHasBeenClicked() throws Exception {
    setupLoadHolderTimelineInteractorCallbacks(timelineWithShots());

    presenter.onHoldingShotsClick();

    verify(streamTimelineView).hideHoldingShots();
  }

  @Test public void shouldHideAllShotsButtonWhenItHasBeenClicked() throws Exception {
    setupDeleteLocalShotsInteractorCallback();
    setupReloadStreamTimelineInteractorCallback();
    presenter.setStreamMode(0);

    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);
    presenter.onAllStreamShotsClick();

    verify(streamTimelineView).hideAllStreamShots();
  }

  @Test public void shouldShowHoldingShotsButtonWhenAllShotsButtonHasBeenClicked()
      throws Exception {
    setupDeleteLocalShotsInteractorCallback();
    setupReloadStreamTimelineInteractorCallback();

    presenter.initialize(streamTimelineView, ID_STREAM, null, PUBLIC);
    presenter.onAllStreamShotsClick();

    verify(streamTimelineView).showHoldingShots();
  }

  @Test public void shouldLoadStreamIfInitializedWithoutAuthorIdUser() throws Exception {
    presenter.initialize(streamTimelineView, ID_STREAM, null, PUBLIC);

    verify(selectStreamInteractor).selectStream(anyString(),
        any(SelectStreamInteractor.Callback.class),
        any(SelectStreamInteractor.ErrorCallback.class));
  }

  @Test public void shouldShowEmptyWhenShotDeletedAndNoMoreShotsInTimeline() throws Exception {
    presenter.onShotDeleted(0);

    verify(streamTimelineView).showEmpty();
  }

  @Test public void shouldHideEmptyWhenShotDeletedAndThereAreShotsInTimeline() throws Exception {
    presenter.onShotDeleted(1);

    verify(streamTimelineView).hideEmpty();
  }

  @Test
  public void shouldNotShowStreamTimelineIndicatorWhenHandleVisibilityIndicatorAndNewShotsNumberIsZero()
      throws Exception {
    presenter.setNewShotsNumber(ZERO_NEW_SHOTS);
    presenter.pause();

    presenter.resume();

    verify(streamTimelineView, never()).showNewShotsIndicator(ZERO_NEW_SHOTS);
  }

  @Test public void shouldCallPollerWhenInitializeWithIdStreamAndAuthor() throws Exception {
    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    verify(poller).init(anyLong(), any(Runnable.class));
  }

  @Test public void shouldCallPollerWhenInitializeWithIdStream() throws Exception {
    presenter.initialize(streamTimelineView, ID_STREAM, null, PUBLIC);

    verify(poller).init(anyLong(), any(Runnable.class));
  }

  @Test public void shouldShowSnackBarWhenInitializeAndStreamTopicIsNotNullAndIsNotEmpty()
      throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_AUTHOR);
    setupGetStreamInteractorCallback();

    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    verify(streamTimelineView).showPinnedMessage(anyString());
  }

  @Test public void shouldNotShowSnackBarWhenInitializeAndStreamTopicIsNull() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_AUTHOR);
    setupGetStreamInteractorCallbackWithoutTopic();

    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    verify(streamTimelineView, never()).showPinnedMessage(anyString());
  }

  @Test public void shouldNotShowSnackBarWhenInitializeAndStreamTopicIsEmpty() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_AUTHOR);
    setupGetStreamInteractorCallbackWithEmptyTopic();

    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    verify(streamTimelineView, never()).showPinnedMessage(anyString());
  }

  @Test public void shouldShowSnackBarWhenEditStreamAndStreamTopicIsNotEmpty() throws Exception {
    setupCreateStreamInteractorCallbackWithTopic();
    setupGetStreamInteractorCallback();
    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    presenter.editStream(TOPIC);

    verify(streamTimelineView).showPinnedMessage(anyString());
  }

  @Test public void shouldNotShowSnackBarWhenEditStreamAndStreamTopicIsEmpty() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_AUTHOR);
    setupCreateStreamInteractorCallbackWithEmptyTopic();
    setupGetStreamInteractorCallbackWithEmptyTopic();
    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    presenter.editStream(EMPTY_TOPIC);

    verify(streamTimelineView, never()).showPinnedMessage(anyString());
  }

  @Test public void shouldSetRemainingCharactersCountWhenTextChanged() throws Exception {
    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    presenter.textChanged(anyString());

    verify(streamTimelineView).setRemainingCharactersCount(anyInt());
  }

  @Test public void shouldSetRemainingCharactersColorValidWhenTextChanged() throws Exception {
    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    presenter.textChanged(anyString());

    verify(streamTimelineView).setRemainingCharactersColorValid();
  }

  @Test public void shouldLoadStreamTimelineWhenResumedWithPausedValue() throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    presenter.initialize(streamTimelineView, ID_STREAM, null, PUBLIC);

    presenter.pause();
    presenter.resume();

    verify(timelineInteractorWrapper, atLeastOnce()).loadTimeline(anyString(), anyBoolean(),
        booleanArgumentCaptor.capture(), anyInt(), anyCallback());
    assertThat(booleanArgumentCaptor.getValue()).isTrue();
  }

  @Test public void shouldLoadStreamTimelineWhenResumedWhitPausedValueAndIsViewOnly()
      throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    presenter.initialize(streamTimelineView, ID_STREAM, null, VIEW_ONLY);
    presenter.setStreamMode(VIEW_ONLY);

    presenter.pause();
    presenter.resume();

    verify(timelineInteractorWrapper, atLeastOnce()).loadTimeline(anyString(), anyBoolean(),
        booleanArgumentCaptor.capture(), anyInt(), anyCallback());
    assertThat(booleanArgumentCaptor.getValue()).isTrue();
  }

  @Test public void shouldLoadStreamTimelineWhenInitializeWithNoPausedValue() throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    presenter.initialize(streamTimelineView, ID_STREAM, null, PUBLIC);

    verify(timelineInteractorWrapper, atLeastOnce()).loadTimeline(anyString(), anyBoolean(),
        booleanArgumentCaptor.capture(), anyInt(), anyCallback());
    assertThat(booleanArgumentCaptor.getValue()).isFalse();
  }

  @Test public void shouldLoadStreamTimelineWhenInitializeWithNoPausedValueAndIsViewOnlyStream()
      throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    presenter.initialize(streamTimelineView, ID_STREAM, null, VIEW_ONLY);
    presenter.setStreamMode(VIEW_ONLY);

    verify(timelineInteractorWrapper, atLeastOnce()).loadTimeline(anyString(), anyBoolean(),
        booleanArgumentCaptor.capture(), anyInt(), anyCallback());
    assertThat(booleanArgumentCaptor.getValue()).isFalse();
  }

  @Test public void shouldRefreshStreamTimelineWhenResumedWithPausedValue() throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    presenter.initialize(streamTimelineView, ID_STREAM, null, PUBLIC);

    presenter.setStreamMode(PUBLIC);
    presenter.pause();
    presenter.resume();

    verify(timelineInteractorWrapper, times(1)).refreshTimeline(anyString(), anyBoolean(), anyLong(),
        booleanArgumentCaptor.capture(), anyInt(), anyCallback(), anyErrorCallback());
    assertThat(booleanArgumentCaptor.getValue()).isTrue();
  }

  @Test public void shouldRefreshStreamTimelineWhenResumedWithPausedValueAndIsViewOnly()
      throws Exception {
    setupSelectStreamInteractorCallbacksStream();

    setupLoadTimelineInteractorCallbacks(timelineWithShots());
    setupRefreshTimelineInteractorCallbacks(timelineWithShots());
    presenter.initialize(streamTimelineView, ID_STREAM, null, VIEW_ONLY);

    presenter.setStreamMode(VIEW_ONLY);
    presenter.pause();
    presenter.resume();

    verify(timelineInteractorWrapper, times(1)).refreshTimeline(anyString(), anyBoolean(), anyLong(),
        booleanArgumentCaptor.capture(), anyInt(), anyCallback(), anyErrorCallback());
    assertThat(booleanArgumentCaptor.getValue()).isTrue();
  }

  @Test public void shouldNotShowPinMessageNotificationWhenTopicIsEmpty() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_AUTHOR);
    setupCreateStreamInteractorCallbackWithTopic();
    setupGetStreamInteractorCallback();
    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    presenter.editStream(EMPTY_TOPIC);

    verify(streamTimelineView, never()).showPinMessageNotification(anyString());
  }

  @Test public void shouldShowTopicSnackBarWhenNotifyMessageAndTopicIsNotEmpty() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_AUTHOR);
    setupCreateStreamInteractorCallbackWithTopic();
    setupGetStreamInteractorCallback();
    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    presenter.notifyMessage(TOPIC, NOTIFY);

    verify(streamTimelineView, times(2)).showPinnedMessage(TOPIC);
  }

  @Test public void shouldHideStreamViewOnlyIndicatorWhenInitializeAndIAmAuthor() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_AUTHOR);
    setupSelectViewOnlyStreamInteractorCallbacksStream();
    setupLoadTimelineInteractorCallbacks(timelineWithShots());

    presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR, PUBLIC);

    verify(streamTimelineView, atLeastOnce()).hideStreamViewOnlyIndicator();
  }

  //region Matchers
  private Interactor.ErrorCallback anyErrorCallback() {
    return any(Interactor.ErrorCallback.class);
  }

  public Interactor.Callback<Timeline> anyCallback() {
    return any(Interactor.Callback.class);
  }

  private Interactor.Callback<List<Contributor>> anyContributorCallback() {
    return any(Interactor.Callback.class);
  }

  private Interactor.Callback<StreamSearchResult> anySelectCallback() {
    return any(Interactor.Callback.class);
  }
  //endregion

  //region Stubs
  private ShotModel lastShotModel() {
    ShotModel shotModel = new ShotModel();
    shotModel.setBirth(LAST_SHOT_DATE);
    return shotModel;
  }

  private Timeline timelineWithShots() {
    Timeline timeline = new Timeline();
    timeline.setShots(shotList());
    return timeline;
  }

  private Timeline emptyTimeline() {
    Timeline timeline = new Timeline();
    timeline.setShots(new ArrayList<Shot>());
    return timeline;
  }

  private List<Shot> shotList() {
    return Arrays.asList(shot());
  }

  private Shot shot() {
    Shot shot = new Shot();
    shot.setPublishDate(new Date());
    shot.setUserInfo(new BaseMessage.BaseMessageUserInfo());
    return shot;
  }

  private StreamSearchResult selectedStream() {
    Stream stream = new Stream();
    stream.setId(SELECTED_STREAM_ID);
    stream.setTopic(TOPIC);
    stream.setTitle(TITLE);
    stream.setDescription(DESCRIPTION);
    stream.setAuthorId(ID_AUTHOR);
    stream.setReadWriteMode("PUBLIC");
    StreamSearchResult streamSearchResult = new StreamSearchResult();
    streamSearchResult.setStream(stream);
    return streamSearchResult;
  }

  private Stream selectedViewOnlyStream() {
    Stream stream = new Stream();
    stream.setId(SELECTED_STREAM_ID);
    stream.setTopic(TOPIC);
    stream.setTitle(TITLE);
    stream.setDescription(DESCRIPTION);
    stream.setAuthorId(ID_AUTHOR);
    stream.setReadWriteMode("VIEWONLY");
    return stream;
  }

  private Stream selectedStreamWithoutTopic() {
    Stream stream = new Stream();
    stream.setId(SELECTED_STREAM_ID);
    stream.setAuthorId(ID_AUTHOR);
    stream.setReadWriteMode("PUBLIC");
    return stream;
  }

  private StreamSearchResult selectedStreamWithEmptyTopic() {
    Stream stream = selectedStream().getStream();
    stream.setReadWriteMode("PUBLIC");
    stream.setTopic("");
    StreamSearchResult streamSearchResult = new StreamSearchResult();
    streamSearchResult.setStream(stream);
    return streamSearchResult;
  }

  private StreamSearchResult streamResult() {
    StreamSearchResult streamSearchResult = new StreamSearchResult();
    streamSearchResult.setStream(selectedStream().getStream());
    return streamSearchResult;
  }

  private StreamSearchResult viewOnlyStreamResult() {
    StreamSearchResult streamSearchResult = new StreamSearchResult();
    streamSearchResult.setStream(selectedViewOnlyStream());
    return streamSearchResult;
  }

  //endregion

  //region Setups
  private void setupGetOlderTimelineInteractorCallbacks(final Timeline timeline) {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.Callback<Timeline>) invocation.getArguments()[4]).onLoaded(timeline);
        return null;
      }
    }).when(timelineInteractorWrapper)
        .obtainOlderTimeline(anyString(), anyBoolean(), anyLong(), anyInt(), anyCallback(), anyErrorCallback());
  }

  private void setupLoadTimelineInteractorCallbacks(final Timeline timeline) {
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.Callback<Timeline>) invocation.getArguments()[4]).onLoaded(timeline);
        return null;
      }
    }).when(timelineInteractorWrapper)
        .loadTimeline(anyString(), anyBoolean(), anyBoolean(), anyInt(), anyCallback());
  }

  private void setupLoadHolderTimelineInteractorCallbacks(final Timeline timeline) {
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.Callback<Timeline>) invocation.getArguments()[4]).onLoaded(timeline);
        return null;
      }
    }).when(streamHoldingTimelineInteractorsWrapper)
        .loadTimeline(anyString(), anyString(), anyBoolean(), anyCallback(), anyErrorCallback());
  }

  private void setupRefreshTimelineInteractorCallbacks(final Timeline timeline) {
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.Callback<Timeline>) invocation.getArguments()[5]).onLoaded(timeline);
        return null;
      }
    }).when(timelineInteractorWrapper)
        .refreshTimeline(anyString(), anyBoolean(), anyLong(), anyBoolean(), anyInt(), anyCallback(),
            anyErrorCallback());
  }

  private void setupSelectStreamInteractorCallbacksStream() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<StreamSearchResult> callback =
            (Interactor.Callback<StreamSearchResult>) invocation.getArguments()[1];
        callback.onLoaded(streamResult());
        return null;
      }
    }).when(selectStreamInteractor)
        .selectStream(anyString(), any(Interactor.Callback.class), anyErrorCallback());
  }

  private void setupSelectViewOnlyStreamInteractorCallbacksStream() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<StreamSearchResult> callback =
            (Interactor.Callback<StreamSearchResult>) invocation.getArguments()[1];
        callback.onLoaded(viewOnlyStreamResult());
        return null;
      }
    }).when(selectStreamInteractor)
        .selectStream(anyString(), any(Interactor.Callback.class), anyErrorCallback());
  }

  private List<Contributor> contributors() {
    Contributor contributor = new Contributor();
    contributor.setIdUser(USER_ID);
    return Collections.singletonList(contributor);
  }

  private void setupDeleteLocalShotsInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback callback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        callback.onCompleted();
        return null;
      }
    }).when(deleteLocalShotsByStreamInteractor)
        .deleteShot(anyString(), any(Interactor.CompletedCallback.class));
  }

  private void setupReloadStreamTimelineInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.Callback<Timeline>) invocation.getArguments()[1]).onLoaded(
            timelineWithShots());
        return null;
      }
    }).when(reloadStreamTimelineInteractor)
        .loadStreamTimeline(anyString(), any(Interactor.Callback.class), anyErrorCallback());
  }

  private void setupGetStreamInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((SelectStreamInteractor.Callback) invocation.getArguments()[1]).onLoaded(selectedStream());
        return null;
      }
    }).when(selectStreamInteractor).selectStream(anyString(), any(SelectStreamInteractor.Callback.class), any(
        Interactor.ErrorCallback.class));
  }

  private void setupGetStreamInteractorCallbackWithoutTopic() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((GetStreamInteractor.Callback) invocation.getArguments()[1]).onLoaded(
            selectedStreamWithoutTopic());
        return null;
      }
    }).when(getStreamInteractor).loadStream(anyString(), any(GetStreamInteractor.Callback.class));
  }

  private void setupGetStreamInteractorCallbackWithEmptyTopic() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((SelectStreamInteractor.Callback) invocation.getArguments()[1]).onLoaded(
            selectedStreamWithEmptyTopic());
        return null;
      }
    }).when(selectStreamInteractor).selectStream(anyString(), any(SelectStreamInteractor.Callback.class), any(
        Interactor.ErrorCallback.class));
  }

  private void setupCreateStreamInteractorCallbackWithTopic() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((UpdateStreamInteractor.Callback) invocation.getArguments()[3]).onLoaded(
            selectedStream().getStream());
        return null;
      }
    }).when(updateStreamInteractor)
        .updateStreamMessage(anyString(), anyString(), anyBoolean(),
            any(UpdateStreamInteractor.Callback.class), anyErrorCallback());
  }

  private void setupCreateStreamInteractorCallbackWithEmptyTopic() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((UpdateStreamInteractor.Callback) invocation.getArguments()[3]).onLoaded(
            selectedStreamWithEmptyTopic().getStream());
        return null;
      }
    }).when(updateStreamInteractor)
        .updateStreamMessage(anyString(), anyString(), anyBoolean(),
            any(UpdateStreamInteractor.Callback.class), anyErrorCallback());
  }

  private void setupFirstShotPosition() {
    presenter.setIsFirstShotPosition(true);
  }

  private void setupIsNotFirstShotPosition() {
    presenter.setIsFirstLoad(false);
    presenter.setIsFirstShotPosition(false);
  }

  private void setupOldListSize() {
    presenter.setOldListSize(OLD_LIST_SIZE);
  }

  private void setupIsNotFirstLoadAndIsFirstPosition() {
    presenter.setIsFirstLoad(false);
    presenter.setIsFirstShotPosition(true);
  }

  private void setupNewShotsNumbers() {
    presenter.setNewShotsNumber(NEW_SHOTS_NUMBER);
  }

  private void setupZeroNewShotsNumbers() {
    presenter.setNewShotsNumber(ZERO_NEW_SHOTS);
  }

  private void setupShotsModels() {
    presenter.setShotModels(new TreeSet<ShotModel>());
  }

  private void setupIsFirstLoad() {
    presenter.setIsFirstLoad(true);
  }

  private void setupIsNotFirstLoad() {
    presenter.setIsFirstLoad(false);
  }
  //endregion
}
