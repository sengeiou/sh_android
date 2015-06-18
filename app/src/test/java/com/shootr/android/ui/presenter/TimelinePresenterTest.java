package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.presenter.interactorwrapper.EventTimelineInteractorsWrapper;
import com.shootr.android.ui.views.TimelineView;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TimelinePresenterTest {

    private static final Date LAST_SHOT_DATE = new Date();
    private static final ShotSent.Event SHOT_SENT_EVENT = null;

    @Mock TimelineView timelineView;
    @Mock EventTimelineInteractorsWrapper timelineInteractorWrapper;
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;

    private TimelinePresenter presenter;
    private ShotSent.Receiver shotSentReceiver;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShotModelMapper shotModelMapper = new ShotModelMapper();
        presenter = new TimelinePresenter(timelineInteractorWrapper, shotModelMapper, bus, errorMessageFactory);
        presenter.setView(timelineView);
        shotSentReceiver = presenter;
    }

    //region Load timeline
    @Test public void shouldLoadTimlelineWhenInitialized() throws Exception {
        presenter.initialize(timelineView);

        verify(timelineInteractorWrapper).loadTimeline(anyCallback(), anyErrorCallback());
    }

    @Test public void shouldRenderTimelineShotsInViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(timelineView).setShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldShowShotsInViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(timelineView).showShots();
    }

    @Test public void shouldShowLoadingViewWhenLoadTimeline() throws Exception {
        presenter.loadTimeline();

        verify(timelineView, times(1)).showLoading();
    }

    @Test public void shouldHideLoadingViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(timelineView, times(1)).hideLoading();
    }

    @Test public void shouldHideLoadingViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(timelineView, times(1)).hideLoading();
    }

    @Test public void shouldShowEmptyViewWhenLoadTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(timelineView).showEmpty();
    }

    @Test public void shouldHideEmtpyViewWhenLoadTimelineRespondsShots() throws Exception {
        setupLoadTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadTimeline();

        verify(timelineView).hideEmpty();
    }

    @Test public void shouldHideTimelineShotsWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        verify(timelineView).hideShots();
    }

    @Test public void shouldRenderEmtpyShotListWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupLoadTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadTimeline();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(timelineView).setShots(captor.capture());
        List renderedShotList = captor.<List<ShotModel>>getValue();
        assertThat(renderedShotList).isEmpty();
    }
    //endregion

    //region Refresh main timeline
    @Test public void shouldAddNewShotsWhenRefreshTimelineRespondsShots() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(timelineView).addNewShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldNotAddNewShotsWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(timelineView, never()).addNewShots(anyListOf(ShotModel.class));
    }

    @Test public void shouldShowLoadingWhenRefreshTimeline() throws Exception {
        presenter.refresh();

        verify(timelineView, times(1)).showLoading();
    }

    @Test public void shouldHideLoadingWhenRefreshTimelineRespondsShots() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(timelineView).hideLoading();
    }

    @Test public void shouldHideLoadingWhenRefreshTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(timelineView).hideLoading();
    }

    @Test public void shouldHideEmptyIfReceivedShotsWhenRefreshTimeline() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(timelineView).hideEmpty();
    }

    @Test public void shouldShowShotsIfReceivedShotsWhenRefresTimeline() throws Exception {
        setupRefreshTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(timelineView).showShots();
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

        verify(timelineView).showLoadingOldShots();
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
    //endregion
}
