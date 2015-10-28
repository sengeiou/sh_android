package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.ShareShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.ui.Poller;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.presenter.interactorwrapper.StreamTimelineInteractorsWrapper;
import com.shootr.android.ui.views.StreamTimelineView;
import com.shootr.android.util.ErrorMessageFactory;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StreamTimelinePresenterTest {

    private static final Date LAST_SHOT_DATE = new Date();
    private static final ShotSent.Event SHOT_SENT_EVENT = null;
    private static final String SELECTED_STREAM_ID = "stream";

    @Mock StreamTimelineView streamTimelineView;
    @Mock StreamTimelineInteractorsWrapper timelineInteractorWrapper;
    @Mock SelectStreamInteractor selectStreamInteractor;
    @Mock MarkNiceShotInteractor markNiceShotInteractor;
    @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    @Mock ShareShotInteractor shareShotInteractor;
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock Poller poller;

    private StreamTimelinePresenter presenter;
    private ShotSent.Receiver shotSentReceiver;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShotModelMapper shotModelMapper = new ShotModelMapper();
        presenter = new StreamTimelinePresenter(timelineInteractorWrapper, selectStreamInteractor,
          markNiceShotInteractor,
          unmarkNiceShotInteractor, shareShotInteractor, shotModelMapper, bus, errorMessageFactory, poller);
        presenter.setView(streamTimelineView);
        shotSentReceiver = presenter;
    }

    //region Select stream

    @Test
    public void shouldSelectStreamWhenInitialized() throws Exception {
        presenter.initialize(streamTimelineView, SELECTED_STREAM_ID);

        verify(selectStreamInteractor).selectStream(eq(SELECTED_STREAM_ID), anySelectCallback());
    }
    //endregion

    //region Load timeline
    @Test public void shouldLoadTimlelineWhenSelectStreamIfSelectStreamCallbacks() throws Exception {
        setupSelectStreamInteractorCallbacksStream();

        presenter.selectStream();

        verify(timelineInteractorWrapper).loadTimeline(anyString(), anyCallback());
    }

    @Test public void shouldRenderTimelineShotsInViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(streamTimelineView).setShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldShowShotsInViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(streamTimelineView).showShots();
    }

    @Test public void shouldNotShowLoadingViewWhenLoadTimeline() throws Exception {
        presenter.loadTimeline();

        verify(streamTimelineView, never()).showLoading();
    }

    @Test public void shouldNotHideLoadingViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(streamTimelineView, never()).hideLoading();
    }

    @Test public void shouldNotHideLoadingViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(streamTimelineView, never()).hideLoading();
    }

    @Test public void shouldShowEmptyViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(streamTimelineView).showEmpty();
    }

    @Test public void shouldHideEmptyViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(streamTimelineView).hideEmpty();
    }

    @Test public void shouldHideEmtpyViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(streamTimelineView).hideEmpty();
    }

    @Test public void shouldHideTimelineShotsWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(streamTimelineView).hideShots();
    }

    @Test public void shouldRenderEmtpyShotListWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(streamTimelineView).setShots(captor.capture());
        List renderedShotList = captor.<List<ShotModel>>getValue();
        assertThat(renderedShotList).isEmpty();
    }
    //endregion

    //region Refresh main timeline
    @Test public void shouldSetShotsWhenRefreshTimelineRespondsShots() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(streamTimelineView).setShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldNotAddNewShotsWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(streamTimelineView, never()).addNewShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldShowLoadingWhenRefreshTimeline() throws Exception {
        presenter.refresh();

        verify(streamTimelineView, times(1)).showLoading();
    }

    @Test public void shouldHideLoadingWhenRefreshTimelineRespondsShots() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(streamTimelineView).hideLoading();
    }

    @Test public void shouldHideLoadingWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(streamTimelineView).hideLoading();
    }

    @Test public void shouldShowShotsIfReceivedShotsWhenRefresTimeline() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(streamTimelineView).showShots();
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
    //endregion

    //region Bus streams
    @Test public void shouldRefreshTimelineWhenShotSent() throws Exception {
        shotSentReceiver.onShotSent(SHOT_SENT_EVENT);

        verify(timelineInteractorWrapper).refreshTimeline(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldShotSentReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = ShotSent.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod = shotSentReceiver.getClass().getMethod(receiverMethodName, ShotSent.Event.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    @Test public void shouldShowHoldingShotsButtonWhenInitialize() throws Exception {
        presenter.initialize(streamTimelineView, SELECTED_STREAM_ID);

        verify(streamTimelineView).showHoldingShots();
    }

    @Test public void shouldHideHolingShotsButtonWhenItHasBeenClicked() throws Exception {
        presenter.onHoldingShotsClick();

        verify(streamTimelineView).hideHoldingShots();
    }

    @Test public void shouldShowAllShotsButtonWhenHoldingShotsButtonHasBeenClicked() throws Exception {
        presenter.onHoldingShotsClick();

        verify(streamTimelineView).showAllStreamShots();
    }

    @Test public void shouldHideAllShotsButtonWhenItHasBeenClicked() throws Exception {
        presenter.onAllStreamShotsClick();

        verify(streamTimelineView).hideAllStreamShots();
    }

    @Test public void shouldShowHoldingShotsButtonWhenAllShotsButtonHasBeenClicked() throws Exception {
        presenter.onAllStreamShotsClick();

        verify(streamTimelineView).showHoldingShots();
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

    private com.shootr.android.domain.Stream selectedStream() {
        com.shootr.android.domain.Stream stream = new com.shootr.android.domain.Stream();
        stream.setId(SELECTED_STREAM_ID);
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
                ((Interactor.Callback<Timeline>) invocation.getArguments()[1]).onLoaded(timeline);
                return null;
            }
        }).when(timelineInteractorWrapper).loadTimeline(anyString(), anyCallback());
    }

    private void setupRefreshTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.Callback<Timeline>) invocation.getArguments()[1]).onLoaded(timeline);
                return null;
            }
        }).when(timelineInteractorWrapper).refreshTimeline(anyString(), anyCallback(), anyErrorCallback());
    }

    private void setupSelectStreamInteractorCallbacksStream() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<StreamSearchResult> callback = (Interactor.Callback<StreamSearchResult>) invocation.getArguments()[1];
                callback.onLoaded(streamResult());
                return null;
            }
        }).when(selectStreamInteractor).selectStream(anyString(), any(Interactor.Callback.class));
    }
    //endregion
}
