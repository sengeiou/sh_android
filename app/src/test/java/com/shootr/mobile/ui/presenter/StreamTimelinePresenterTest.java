package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.bus.ShotSent;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.DeleteLocalShotsByStream;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.timeline.ReloadStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.UpdateWatchNumberInteractor;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.StreamHoldingTimelineInteractorsWrapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.StreamTimelineInteractorsWrapper;
import com.shootr.mobile.ui.views.StreamTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    private static final String SHORT_TITLE = "shortTitle";
    private static final String DESCRIPTION = "description";
    private static final String EMPTY_TOPIC = "";
    private static final boolean NOTIFY = true;

    @Mock StreamTimelineView streamTimelineView;
    @Mock StreamTimelineInteractorsWrapper timelineInteractorWrapper;
    @Mock SelectStreamInteractor selectStreamInteractor;
    @Mock MarkNiceShotInteractor markNiceShotInteractor;
    @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    @Mock ShareShotInteractor shareShotInteractor;
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock Poller poller;
    @Mock StreamHoldingTimelineInteractorsWrapper streamHoldingTimelineInteractorsWrapper;
    @Mock DeleteLocalShotsByStream deleteLocalShotsByStream;
    @Mock ReloadStreamTimelineInteractor reloadStreamTimelineInteractor;
    @Mock GetStreamInteractor getStreamInteractor;
    @Mock UpdateWatchNumberInteractor updateWatchNumberInteractor;
    @Mock CreateStreamInteractor createStreamInteractor;
    @Captor ArgumentCaptor<Boolean> booleanArgumentCaptor;

    private StreamTimelinePresenter presenter;
    private ShotSent.Receiver shotSentReceiver;
    private Boolean NOT_PAUSED = false;
    private Long LAST_REFRESH_DATE = 0L;
    private Boolean PAUSED = true;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShotModelMapper shotModelMapper = new ShotModelMapper();
        presenter = new StreamTimelinePresenter(timelineInteractorWrapper,
          streamHoldingTimelineInteractorsWrapper,
          selectStreamInteractor,
          markNiceShotInteractor,
          unmarkNiceShotInteractor,
          shareShotInteractor,
          getStreamInteractor,
          shotModelMapper,
          bus,
          errorMessageFactory,
          poller,
          deleteLocalShotsByStream,
          updateWatchNumberInteractor,
          reloadStreamTimelineInteractor,
          createStreamInteractor);
        presenter.setView(streamTimelineView);
        shotSentReceiver = presenter;
    }

    //region Select stream

    @Test public void shouldSelectStreamWhenInitialized() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.initialize(streamTimelineView, ID_STREAM, SELECTED_STREAM_ID);

        verify(selectStreamInteractor).selectStream(eq(ID_STREAM), anySelectCallback(), anyErrorCallback());
    }
    //endregion

    //region Load timeline
    @Test public void shouldLoadTimlelineWhenSelectStreamIfSelectStreamCallbacks() throws Exception {
        setupSelectStreamInteractorCallbacksStream();

        presenter.selectStream();

        verify(timelineInteractorWrapper).loadTimeline(anyString(), anyBoolean(), anyCallback());
    }

    @Test public void shouldRenderTimelineShotsInViewWhenLoadTimelineRespondsShotsAndIsFirstPosition()
      throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupFirstShotPosition();

        presenter.loadTimeline();

        verify(streamTimelineView).setShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldRenderTimelineShotsInViewWhenLoadTimelineRespondsShotsAndIsNotFirstPosition()
      throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupIsNotFirstShotPosition();
        setupOldListSize();
        setupNewShotsNumbers();

        presenter.loadTimeline();

        verify(streamTimelineView).setShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldShowShotsInViewWhenLoadTimelineRespondsShotsAndFirstPosition() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupFirstShotPosition();

        presenter.loadTimeline();

        verify(streamTimelineView).showShots();
    }

    @Test public void shouldShowShotsInViewWhenLoadTimelineRespondsShotsAndIsNotFirstPosition() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupIsNotFirstShotPosition();
        setupOldListSize();
        setupNewShotsNumbers();

        presenter.loadTimeline();

        verify(streamTimelineView, never()).showShots();
    }

    @Test public void shouldSetPositionInViewWhenLoadTimelineRespondsShotsAndIsNotFirstPosition() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupIsNotFirstShotPosition();
        setupOldListSize();
        setupNewShotsNumbers();

        presenter.loadTimeline();

        verify(streamTimelineView).setPosition(NEW_SHOTS_NUMBER, timelineWithShots().getShots().size());
    }

    @Test public void shouldNotShowLoadingViewWhenLoadTimeline() throws Exception {
        presenter.loadTimeline();

        verify(streamTimelineView, never()).showLoading();
    }

    @Test public void shouldNotHideLoadingViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupFirstShotPosition();

        presenter.loadTimeline();

        verify(streamTimelineView, never()).hideLoading();
    }

    @Test public void shouldNotHideLoadingViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());
        setupFirstShotPosition();

        presenter.loadTimeline();

        verify(streamTimelineView, never()).hideLoading();
    }

    @Test public void shouldShowEmptyViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());
        setupFirstShotPosition();

        presenter.loadTimeline();

        verify(streamTimelineView).showEmpty();
    }

    @Test public void shouldHideEmptyViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupFirstShotPosition();

        presenter.loadTimeline();

        verify(streamTimelineView).hideEmpty();
    }

    @Test public void shouldHideEmtpyViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupFirstShotPosition();

        presenter.loadTimeline();

        verify(streamTimelineView).hideEmpty();
    }

    @Test public void shouldHideTimelineShotsWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());
        setupFirstShotPosition();

        presenter.loadTimeline();

        verify(streamTimelineView).hideShots();
    }

    @Test public void shouldRenderEmtpyShotListWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());
        setupFirstShotPosition();

        presenter.loadTimeline();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(streamTimelineView).setShots(captor.capture());
        List renderedShotList = captor.<List<ShotModel>>getValue();
        assertThat(renderedShotList).isEmpty();
    }
    //endregion

    //region Refresh main timeline
    @Test public void shouldSetShotsWhenRefreshTimelineRespondsShotsAndIsFirstPosition() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupIsNotFirstLoadAndIsFirstPosition();

        presenter.initialize(streamTimelineView, ID_STREAM);
        presenter.refresh();

        verify(streamTimelineView, times(3)).setShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldSetShotsWhenRefreshTimelineRespondsShotsAndIsNotFirstPosition() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupIsNotFirstShotPosition();
        setupOldListSize();
        setupNewShotsNumbers();

        presenter.refresh();

        verify(streamTimelineView).setShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldNotAddNewShotsWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(streamTimelineView, never()).addNewShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldNotShowLoadingWhenRefreshTimeline() throws Exception {
        presenter.refresh();

        verify(streamTimelineView, never()).showLoading();
    }

    @Test public void shouldHideLoadingWhenRefreshTimelineRespondsShots() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);
        presenter.refresh();

        verify(streamTimelineView).hideLoading();
    }

    @Test public void shouldHideLoadingWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(streamTimelineView).hideLoading();
    }

    @Test public void shouldNotShowShotsIfReceivedShotsWhenRefresTimelineAndIsNotInFirstPosition() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupIsNotFirstShotPosition();
        setupOldListSize();
        setupNewShotsNumbers();

        presenter.refresh();

        verify(streamTimelineView, never()).showShots();
    }

    @Test public void shouldShowStreamTimelineIndicatorWhenRefreshTimelineAndIsNotInFirstPosition() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupIsNotFirstShotPosition();
        setupOldListSize();
        setupNewShotsNumbers();

        presenter.refresh();

        verify(streamTimelineView).showTimelineIndicator(anyInt());
    }

    @Test public void shouldNotShowStreamTimelineIndicatorWhenRefreshTimelineAndIsInFirstPosition() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupFirstShotPosition();
        setupOldListSize();
        setupNewShotsNumbers();

        presenter.refresh();

        verify(streamTimelineView, never()).showTimelineIndicator(anyInt());
    }

    @Test public void shouldNotShowStreamTimelineIndicatorWhenRefreshTimelineAndNumberNewShotsIsZero()
      throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupIsNotFirstShotPosition();
        setupOldListSize();
        setupZeroNewShotsNumbers();

        presenter.refresh();

        verify(streamTimelineView, never()).showTimelineIndicator(ZERO_NEW_SHOTS);
    }

    //endregion

    //region Older shots
    @Test public void shouldObtainOlderTimelineWhenShowingLastShot() throws Exception {
        presenter.showingLastShot(lastShotModel());

        verify(timelineInteractorWrapper).obtainOlderTimeline(anyLong(), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldObtainOlderTimelineOnceWhenShowingLastShotTwiceWithoutCallbackExecuted() throws Exception {
        presenter.showingLastShot(lastShotModel());
        presenter.showingLastShot(lastShotModel());

        verify(timelineInteractorWrapper, times(1)).obtainOlderTimeline(anyLong(), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldShowLoadingOlderShotsWhenShowingLastShot() throws Exception {
        presenter.showingLastShot(lastShotModel());

        verify(streamTimelineView).showLoadingOldShots();
    }

    @Test public void shouldObtainOlderTimelineOnlyOnceWhenCallbacksEmptyList() throws Exception {
        setupGetOlderTimelineInteractorCallbacks(emptyTimeline());

        presenter.showingLastShot(lastShotModel());
        presenter.showingLastShot(lastShotModel());

        verify(timelineInteractorWrapper, times(1)).obtainOlderTimeline(anyLong(), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldObtainHolderOlderTimelineWhenShowingLastShot() throws Exception {
        presenter.setIdAuthor(ID_AUTHOR);
        presenter.showingHolderShots(true);

        presenter.showingLastShot(lastShotModel());

        verify(streamHoldingTimelineInteractorsWrapper).obtainOlderTimeline(anyLong(),
          anyString(),
          anyCallback(),
          anyErrorCallback());
    }

    @Test public void shouldObtainHolderOlderTimelineOnceWhenShowingLastShotTwiceWithoutCallbackExecuted()
      throws Exception {
        presenter.setIdAuthor(ID_AUTHOR);
        presenter.showingHolderShots(true);

        presenter.showingLastShot(lastShotModel());
        presenter.showingLastShot(lastShotModel());

        verify(streamHoldingTimelineInteractorsWrapper, times(1)).obtainOlderTimeline(anyLong(),
          anyString(),
          anyCallback(),
          anyErrorCallback());
    }

    @Test public void shouldObtainHolderOlderTimelineOnlyOnceWhenCallbacksEmptyList() throws Exception {
        setupGetOlderTimelineInteractorCallbacks(emptyTimeline());
        presenter.setIdAuthor(ID_AUTHOR);
        presenter.showingHolderShots(true);

        presenter.showingLastShot(lastShotModel());
        presenter.showingLastShot(lastShotModel());

        verify(streamHoldingTimelineInteractorsWrapper, times(1)).obtainOlderTimeline(anyLong(),
          anyString(),
          anyCallback(),
          anyErrorCallback());
    }
    //endregion

    //region Bus streams
    @Test public void shouldRefreshTimelineWhenShotSent() throws Exception {
        shotSentReceiver.onShotSent(SHOT_SENT_EVENT);

        verify(timelineInteractorWrapper).refreshTimeline(anyString(),
          anyLong(),
          anyBoolean(),
          anyCallback(),
          anyErrorCallback());
    }

    @Test public void shouldRefreshTimelineWhenPaused() throws Exception {
        presenter.setNewShotsNumber(ZERO_NEW_SHOTS);
        presenter.pause();

        presenter.resume();

        verify(timelineInteractorWrapper).loadTimeline(anyString(), anyBoolean(), anyCallback());
    }

    @Test public void shouldShotSentReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = ShotSent.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod = shotSentReceiver.getClass().getMethod(receiverMethodName, ShotSent.Event.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    @Test public void shouldShowHoldingShotsButtonWhenInitialize() throws Exception {
        presenter.initialize(streamTimelineView, ID_STREAM, SELECTED_STREAM_ID);

        verify(streamTimelineView).showHoldingShots();
    }

    @Test public void shouldHideHolingShotsButtonWhenItHasBeenClicked() throws Exception {
        setupLoadHolderTimelineInteractorCallbacks(timelineWithShots());

        presenter.onHoldingShotsClick();

        verify(streamTimelineView).hideHoldingShots();
    }

    @Test public void shouldShowAllShotsButtonWhenHoldingShotsButtonHasBeenClicked() throws Exception {
        setupLoadHolderTimelineInteractorCallbacks(timelineWithShots());

        presenter.onHoldingShotsClick();

        verify(streamTimelineView).showAllStreamShots();
    }

    @Test public void shouldHideAllShotsButtonWhenItHasBeenClicked() throws Exception {
        setupDeleteLocalShotsInteractorCallback();
        setupReloadStreamTimelineInteractorCallback();

        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);
        presenter.onAllStreamShotsClick();

        verify(streamTimelineView).hideAllStreamShots();
    }

    @Test public void shouldShowHoldingShotsButtonWhenAllShotsButtonHasBeenClicked() throws Exception {
        setupDeleteLocalShotsInteractorCallback();
        setupReloadStreamTimelineInteractorCallback();

        presenter.initialize(streamTimelineView, ID_STREAM);
        presenter.onAllStreamShotsClick();

        verify(streamTimelineView, times(2)).showHoldingShots();
    }

    @Test public void shouldShowLoadingWhenAllStreamShotsClicked() throws Exception {
        presenter.onAllStreamShotsClick();

        verify(streamTimelineView).showLoading();
    }

    @Test public void shouldHideLoadingWhenAllStreamShotsClickedAndallbackReturned() throws Exception {
        setupDeleteLocalShotsInteractorCallback();
        setupReloadStreamTimelineInteractorCallback();

        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);
        presenter.onAllStreamShotsClick();

        verify(streamTimelineView).hideLoading();
    }

    @Test public void shouldLoadStreamIfInitializedWithoutAuthorIdUser() throws Exception {
        presenter.initialize(streamTimelineView, ID_STREAM);

        verify(getStreamInteractor).loadStream(anyString(), any(GetStreamInteractor.Callback.class));
    }

    @Test public void shouldSetToolbarTitleIfInitializedWithoutAuthorIdUser() throws Exception {
        setupGetStreamInteractorCallback();

        presenter.initialize(streamTimelineView, ID_STREAM);

        verify(streamTimelineView).setTitle(anyString());
    }

    @Test public void shouldShowEmptyWhenShotDeletedAndNoMoreShotsInTimeline() throws Exception {
        presenter.onShotDeleted(0);

        verify(streamTimelineView).showEmpty();
    }

    @Test public void shouldHideEmptyWhenShotDeletedAndThereAreShotsInTimeline() throws Exception {
        presenter.onShotDeleted(1);

        verify(streamTimelineView).hideEmpty();
    }

    @Test public void shouldNotShowStreamTimelineIndicatorWhenHandleVisibilityIndicatorAndNewShotsNumberIsZero()
      throws Exception {
        presenter.setNewShotsNumber(ZERO_NEW_SHOTS);
        presenter.pause();

        presenter.resume();

        verify(streamTimelineView, never()).showTimelineIndicator(ZERO_NEW_SHOTS);
    }

    @Test public void shouldCallPollerWhenInitializeWithIdStreamAndAuthor() throws Exception {
        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        verify(poller).init(anyLong(), any(Runnable.class));
    }

    @Test public void shouldCallPollerWhenInitializeWithIdStream() throws Exception {
        presenter.initialize(streamTimelineView, ID_STREAM);

        verify(poller).init(anyLong(), any(Runnable.class));
    }

    @Test public void shouldShowSnackBarWhenInitializeAndStreamTopicIsNotNullAndIsNotEmpty() throws Exception {
        setupGetStreamInteractorCallback();

        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        verify(streamTimelineView).showTopicSnackBar(anyString());
    }

    @Test public void shouldNotShowSnackBarWhenInitializeAndStreamTopicIsNull() throws Exception {
        setupGetStreamInteractorCallbackWithoutTopic();

        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        verify(streamTimelineView, never()).showTopicSnackBar(anyString());
    }

    @Test public void shouldNotShowSnackBarWhenInitializeAndStreamTopicIsEmpty() throws Exception {
        setupGetStreamInteractorCallbackWithEmptyTopic();

        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        verify(streamTimelineView, never()).showTopicSnackBar(anyString());
    }

    @Test public void shouldShowSnackBarWhenEditStreamAndStreamTopicIsNotEmpty() throws Exception {
        setupCreateStreamInteractorCallbackWithTopic();
        setupGetStreamInteractorCallback();
        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        presenter.editStream(TOPIC);

        verify(streamTimelineView).showTopicSnackBar(anyString());
    }

    @Test public void shouldNotShowSnackBarWhenEditStreamAndStreamTopicIsEmpty() throws Exception {
        setupCreateStreamInteractorCallbackWithEmptyTopic();
        setupGetStreamInteractorCallbackWithEmptyTopic();
        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        presenter.editStream(EMPTY_TOPIC);

        verify(streamTimelineView, never()).showTopicSnackBar(anyString());
    }

    @Test public void shouldSetRemainingCharactersCountWhenTextChanged() throws Exception {
        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        presenter.textChanged(anyString());

        verify(streamTimelineView).setRemainingCharactersCount(anyInt());
    }

    @Test public void shouldSetRemainingCharactersColorValidWhenTextChanged() throws Exception {
        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        presenter.textChanged(anyString());

        verify(streamTimelineView).setRemainingCharactersColorValid();
    }

    @Test public void shouldLoadStreamTimelineWhenResumedWithPausedValue() throws Exception {
        presenter.initialize(streamTimelineView, ID_STREAM);

        presenter.pause();
        presenter.resume();

        verify(timelineInteractorWrapper, atLeastOnce()).loadTimeline(anyString(),
          booleanArgumentCaptor.capture(),
          anyCallback());
        assertThat(booleanArgumentCaptor.getValue()).isTrue();
    }

    @Test public void shouldLoadStreamTimelineWhenInitializeWithNoPausedValue() throws Exception {
        presenter.initialize(streamTimelineView, ID_STREAM);

        verify(timelineInteractorWrapper, atLeastOnce()).loadTimeline(anyString(),
          booleanArgumentCaptor.capture(),
          anyCallback());
        assertThat(booleanArgumentCaptor.getValue()).isFalse();
    }

    @Test public void shouldRefreshStreamTimelineWhenResumedWithPausedValue() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        presenter.initialize(streamTimelineView, ID_STREAM);

        presenter.pause();
        presenter.resume();

        verify(timelineInteractorWrapper, times(2)).refreshTimeline(anyString(),
          anyLong(),
          booleanArgumentCaptor.capture(),
          anyCallback(),
          anyErrorCallback());
        assertThat(booleanArgumentCaptor.getValue()).isTrue();
    }

    @Test public void shouldNotShowNotificationConfirmationWhenTopicIsEmpty() throws Exception {
        setupCreateStreamInteractorCallbackWithTopic();
        setupGetStreamInteractorCallback();
        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        presenter.editStream(EMPTY_TOPIC);

        verify(streamTimelineView, never()).showNotificationConfirmation(anyString());
    }

    @Test public void shouldShowTopicSnackBarWhenNotifyMessageAndTopicIsNotEmpty() throws Exception {
        setupCreateStreamInteractorCallbackWithTopic();
        setupGetStreamInteractorCallback();
        presenter.initialize(streamTimelineView, ID_STREAM, ID_AUTHOR);

        presenter.notifyMessage(TOPIC, NOTIFY);

        verify(streamTimelineView, times(2)).showTopicSnackBar(TOPIC);
    }
    
    //region Matchers
    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    public Interactor.Callback<Timeline> anyCallback() {
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
        shot.setUserInfo(new Shot.ShotUserInfo());
        return shot;
    }

    private Stream selectedStream() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        stream.setTopic(TOPIC);
        stream.setTitle(TITLE);
        stream.setShortTitle(SHORT_TITLE);
        stream.setDescription(DESCRIPTION);
        return stream;
    }

    private Stream selectedStreamWithoutTopic() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        return stream;
    }

    private Stream selectedStreamWithEmptyTopic() {
        Stream stream = selectedStream();
        stream.setTopic("");
        return stream;
    }

    private StreamSearchResult streamResult() {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        streamSearchResult.setStream(selectedStream());
        return streamSearchResult;
    }

    //endregion

    //region Setups
    private void setupGetOlderTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.Callback<Timeline>) invocation.getArguments()[1]).onLoaded(timeline);
                return null;
            }
        }).when(timelineInteractorWrapper).obtainOlderTimeline(anyLong(), anyCallback(), anyErrorCallback());
    }

    private void setupLoadTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.Callback<Timeline>) invocation.getArguments()[2]).onLoaded(timeline);
                return null;
            }
        }).when(timelineInteractorWrapper).loadTimeline(anyString(), anyBoolean(), anyCallback());
    }

    private void setupLoadHolderTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.Callback<Timeline>) invocation.getArguments()[3]).onLoaded(timeline);
                return null;
            }
        }).when(streamHoldingTimelineInteractorsWrapper)
          .loadTimeline(anyString(), anyString(), anyBoolean(), anyCallback(), anyErrorCallback());
    }

    private void setupRefreshTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.Callback<Timeline>) invocation.getArguments()[3]).onLoaded(timeline);
                return null;
            }
        }).when(timelineInteractorWrapper)
          .refreshTimeline(anyString(), anyLong(), anyBoolean(), anyCallback(), anyErrorCallback());
    }

    private void setupSelectStreamInteractorCallbacksStream() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<StreamSearchResult> callback =
                  (Interactor.Callback<StreamSearchResult>) invocation.getArguments()[1];
                callback.onLoaded(streamResult());
                return null;
            }
        }).when(selectStreamInteractor).selectStream(anyString(), any(Interactor.Callback.class), anyErrorCallback());
    }

    private void setupDeleteLocalShotsInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback = (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(deleteLocalShotsByStream).deleteShot(anyString(), any(Interactor.CompletedCallback.class));
    }

    private void setupReloadStreamTimelineInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.Callback<Timeline>) invocation.getArguments()[1]).onLoaded(timelineWithShots());
                return null;
            }
        }).when(reloadStreamTimelineInteractor)
          .loadStreamTimeline(anyString(), any(Interactor.Callback.class), anyErrorCallback());
    }

    private void setupGetStreamInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetStreamInteractor.Callback) invocation.getArguments()[1]).onLoaded(selectedStream());
                return null;
            }
        }).when(getStreamInteractor).loadStream(anyString(), any(GetStreamInteractor.Callback.class));
    }

    private void setupGetStreamInteractorCallbackWithoutTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetStreamInteractor.Callback) invocation.getArguments()[1]).onLoaded(selectedStreamWithoutTopic());
                return null;
            }
        }).when(getStreamInteractor).loadStream(anyString(), any(GetStreamInteractor.Callback.class));
    }

    private void setupGetStreamInteractorCallbackWithEmptyTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetStreamInteractor.Callback) invocation.getArguments()[1]).onLoaded(selectedStreamWithEmptyTopic());
                return null;
            }
        }).when(getStreamInteractor).loadStream(anyString(), any(GetStreamInteractor.Callback.class));
    }

    private void setupCreateStreamInteractorCallbackWithTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CreateStreamInteractor.Callback) invocation.getArguments()[7]).onLoaded(selectedStream());
                return null;
            }
        }).when(createStreamInteractor)
          .sendStream(anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyBoolean(),
            anyBoolean(),
            any(CreateStreamInteractor.Callback.class),
            anyErrorCallback());
    }

    private void setupCreateStreamInteractorCallbackWithEmptyTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CreateStreamInteractor.Callback) invocation.getArguments()[7]).onLoaded(selectedStreamWithEmptyTopic());
                return null;
            }
        }).when(createStreamInteractor)
          .sendStream(anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyBoolean(),
            anyBoolean(),
            any(CreateStreamInteractor.Callback.class),
            anyErrorCallback());
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

    //endregion
}
