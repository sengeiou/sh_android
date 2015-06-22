package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.presenter.interactorwrapper.EventTimelineInteractorsWrapper;
import com.shootr.android.ui.views.EventTimelineView;
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

public class EventTimelinePresenterTest {

    private static final Date LAST_SHOT_DATE = new Date();
    private static final ShotSent.Event SHOT_SENT_EVENT = null;
    private static final String SELECTED_EVENT_ID = "event";

    @Mock EventTimelineView eventTimelineView;
    @Mock EventTimelineInteractorsWrapper timelineInteractorWrapper;
    @Mock SelectEventInteractor selectEventInteractor;
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;

    private EventTimelinePresenter presenter;
    private ShotSent.Receiver shotSentReceiver;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShotModelMapper shotModelMapper = new ShotModelMapper();
        presenter = new EventTimelinePresenter(timelineInteractorWrapper,
          selectEventInteractor,
          shotModelMapper, bus, errorMessageFactory);
        presenter.setView(eventTimelineView);
        shotSentReceiver = presenter;
    }

    //region Select event

    @Test
    public void shouldSelectEventWhenInitialized() throws Exception {
        presenter.initialize(eventTimelineView, SELECTED_EVENT_ID);

        verify(selectEventInteractor).selectEvent(eq(SELECTED_EVENT_ID), anySelectCallback());
    }
    //endregion

    //region Load timeline
    @Test public void shouldLoadTimlelineWhenSelectEventIfSelectEventCallbacks() throws Exception {
        setupSelectEventInteractorCallbacksEvent();

        presenter.selectEvent();

        verify(timelineInteractorWrapper).loadTimeline(anyCallback(), anyErrorCallback());
    }

    @Test public void shouldRenderTimelineShotsInViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(eventTimelineView).setShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldShowShotsInViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(eventTimelineView).showShots();
    }

    @Test public void shouldShowLoadingViewWhenLoadTimeline() throws Exception {
        presenter.loadTimeline();

        verify(eventTimelineView, times(1)).showLoading();
    }

    @Test public void shouldHideLoadingViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(eventTimelineView, times(1)).hideLoading();
    }

    @Test public void shouldHideLoadingViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(eventTimelineView, times(1)).hideLoading();
    }

    @Test public void shouldShowEmptyViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(eventTimelineView).showEmpty();
    }

    @Test public void shouldHideEmtpyViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(eventTimelineView).hideEmpty();
    }

    @Test public void shouldHideTimelineShotsWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(eventTimelineView).hideShots();
    }

    @Test public void shouldRenderEmtpyShotListWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(eventTimelineView).setShots(captor.capture());
        List renderedShotList = captor.<List<ShotModel>>getValue();
        assertThat(renderedShotList).isEmpty();
    }
    //endregion

    //region Refresh main timeline
    @Test public void shouldAddNewShotsWhenRefreshTimelineRespondsShots() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(eventTimelineView).addNewShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldNotAddNewShotsWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(eventTimelineView, never()).addNewShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldShowLoadingWhenRefreshTimeline() throws Exception {
        presenter.refresh();

        verify(eventTimelineView, times(1)).showLoading();
    }

    @Test public void shouldHideLoadingWhenRefreshTimelineRespondsShots() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(eventTimelineView).hideLoading();
    }

    @Test public void shouldHideLoadingWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(eventTimelineView).hideLoading();
    }

    @Test public void shouldHideEmptyIfReceivedShotsWhenRefreshTimeline() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(eventTimelineView).hideEmpty();
    }

    @Test public void shouldShowShotsIfReceivedShotsWhenRefresTimeline() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(eventTimelineView).showShots();
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

        verify(eventTimelineView).showLoadingOldShots();
    }

    @Test public void shouldObtainOlderTimelineOnlyOnceWhenCallbacksEmptyList() throws Exception {
        setupGetOlderTimelineInteractorCallbacks(emptyTimeline());

        presenter.showingLastShot(lastShotModel());
        presenter.showingLastShot(lastShotModel());

        verify(timelineInteractorWrapper, times(1)).obtainOlderTimeline(anyLong(), anyCallback(), anyErrorCallback());
    }
    //endregion

    //region Bus events
    @Test public void shouldRefreshTimelineWhenShotSent() throws Exception {
        shotSentReceiver.onShotSent(SHOT_SENT_EVENT);

        verify(timelineInteractorWrapper).refreshTimeline(anyCallback(), anyErrorCallback());
    }

    @Test public void shouldShotSentReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = ShotSent.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod = shotSentReceiver.getClass().getMethod(receiverMethodName, ShotSent.Event.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    //region Matchers
    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    public Interactor.Callback<Timeline> anyCallback() {
        return any(Interactor.Callback.class);
    }

    private Interactor.Callback<EventSearchResult> anySelectCallback() {
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

    private Event selectedEvent() {
        Event event = new Event();
        event.setId(SELECTED_EVENT_ID);
        return event;
    }

    private EventSearchResult eventResult() {
        EventSearchResult eventSearchResult = new EventSearchResult();
        eventSearchResult.setEvent(selectedEvent());
        return eventSearchResult;
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
                ((Interactor.Callback<Timeline>) invocation.getArguments()[0]).onLoaded(timeline);
                return null;
            }
        }).when(timelineInteractorWrapper).loadTimeline(anyCallback(), anyErrorCallback());
    }

    private void setupRefreshTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.Callback<Timeline>) invocation.getArguments()[0]).onLoaded(timeline);
                return null;
            }
        }).when(timelineInteractorWrapper).refreshTimeline(anyCallback(), anyErrorCallback());
    }

    private void setupSelectEventInteractorCallbacksEvent() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<EventSearchResult> callback = (Interactor.Callback<EventSearchResult>) invocation.getArguments()[1];
                callback.onLoaded(eventResult());
                return null;
            }
        }).when(selectEventInteractor).selectEvent(anyString(), any(Interactor.Callback.class));
    }
    //endregion
}
