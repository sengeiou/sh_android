package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.bus.EventChanged;
import com.shootr.android.domain.bus.ShotSent;
import com.shootr.android.domain.interactor.timeline.GetMainTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.GetOlderMainTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.RefreshMainTimelineInteractor;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.TimelineView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
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
    private static final EventChanged.Event EVENT_CHANGED_EVENT = null;

    @Mock TimelineView timelineView;
    @Mock GetMainTimelineInteractor getMainTimelineInteractor;
    @Mock RefreshMainTimelineInteractor refreshMainTimelineInteractor;
    @Mock GetOlderMainTimelineInteractor getOlderMainTimelineInteractor;
    @Mock Bus bus;

    private TimelinePresenter presenter;
    private ShotSent.Receiver shotSentReceiver;
    private EventChanged.Receiver eventChangedReceiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShotModelMapper shotModelMapper = new ShotModelMapper();
        presenter = new TimelinePresenter(getMainTimelineInteractor, refreshMainTimelineInteractor,
          getOlderMainTimelineInteractor,
          shotModelMapper, bus);
        presenter.setView(timelineView);
        shotSentReceiver = presenter;
        eventChangedReceiver = presenter;
    }

    //region Get main timeline
    @Test
    public void shouldGetMainTimlelineWhenInitialized() throws Exception {
        presenter.initialize(timelineView);

        verify(getMainTimelineInteractor).loadMainTimeline(any(GetMainTimelineInteractor.Callback.class));
    }

    @Test
    public void shouldRenderTimelineShotsInViewWhenGetMainTimelineResponsShots() throws Exception {
        setupGetMainTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadMainTimeline();

        verify(timelineView).setShots(anyListOf(ShotModel.class));
    }

    @Test
    public void shouldShowLoadingViewWhenLoadMainTimeline() throws Exception {
        presenter.loadMainTimeline();

        verify(timelineView, times(1)).showLoading();
    }

    @Test
    public void shouldHideLoadingViewWhenGetMainTimelineRespondsShots() throws Exception {
        setupGetMainTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadMainTimeline();

        verify(timelineView, times(1)).hideLoading();
    }

    @Test
    public void shouldHideLoadingViewWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupGetMainTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadMainTimeline();

        verify(timelineView, times(1)).hideLoading();
    }

    @Test
    public void shouldShowEmptyViewWhenGetMainTimelineRespondsEmptyShotList() throws Exception {
        setupGetMainTimelineInteractorCallbacks(emptyTimeline());

        presenter.loadMainTimeline();

        verify(timelineView).showEmpty();
    }

    @Test
    public void shouldHideEmtpyViewWhenGetMainTimelineRespondsShots() throws Exception {
        setupGetMainTimelineInteractorCallbacks(timelineWithShots());

        presenter.loadMainTimeline();

        verify(timelineView).hideEmpty();
    }

    //endregion

    //region Refresh main timeline
    @Test
    public void shouldAddNewShotsWhenRefreshMainTimelineRespondsShots() throws Exception {
        setupRefreshMainTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(timelineView).addNewShots(anyListOf(ShotModel.class));
    }

    @Test
    public void shouldNotAddNewShotsWhenRefreshMainTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshMainTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(timelineView, never()).addNewShots(anyListOf(ShotModel.class));
    }

    @Test
    public void shouldShowLoadingWhenRefresh() throws Exception {
        presenter.refresh();

        verify(timelineView, times(1)).showLoading();
    }

    @Test
    public void shouldHideLoadingWhenRefreshMainTimelineRespondsShots() throws Exception {
        setupRefreshMainTimelineInteractorCallbacks(timelineWithShots());

        presenter.refresh();

        verify(timelineView).hideLoading();
    }

    @Test
    public void shouldHideLoadingWhenRefreshMainTimelineRespondsEmptyShotList() throws Exception {
        setupRefreshMainTimelineInteractorCallbacks(emptyTimeline());

        presenter.refresh();

        verify(timelineView).hideLoading();
    }
    //endregion

    @Test
    public void shouldLoadMoreShotsWhenLastShotVisiblePositionIsLastShot() throws Exception {
        presenter.showingLastShot(lastShotModel());

        verify(getOlderMainTimelineInteractor).loadOlderMainTimeline(anyLong(),
          any(GetOlderMainTimelineInteractor.Callback.class));
    }

    @Test
    public void shouldLoadOlderShotsOnceWhenShowinLastShotTwiceWithoutCallbackExecuted() throws Exception {
        presenter.showingLastShot(lastShotModel());
        presenter.showingLastShot(lastShotModel());

        verify(getOlderMainTimelineInteractor, times(1)).loadOlderMainTimeline(anyLong(), any(GetOlderMainTimelineInteractor.Callback.class));
    }

    @Test
    public void shouldShowLoadingOlderShotsWhenShowingLastShot() throws Exception {
        presenter.showingLastShot(lastShotModel());

        verify(timelineView).showLoadingOldShots();
    }

    @Test
    public void shouldLoadOlderShotsOnlyOnceWhenCallbacksEmptyList() throws Exception {
        setupGetOlderTimelineInteractorCallbacks(emptyTimeline());

        presenter.showingLastShot(lastShotModel());
        presenter.showingLastShot(lastShotModel());

        verify(getOlderMainTimelineInteractor, times(1)).loadOlderMainTimeline(anyLong(),
          any(GetOlderMainTimelineInteractor.Callback.class));
    }

    @Test
    public void shouldRefreshTimelineWhenShotSent() throws Exception {
        shotSentReceiver.onShotSent(SHOT_SENT_EVENT);

        verify(refreshMainTimelineInteractor).refreshMainTimeline(any(RefreshMainTimelineInteractor.Callback.class));
    }

    @Test
    public void shouldReloadMainTimelineWhenEventChanged() throws Exception {
        presenter.onEventChanged(EVENT_CHANGED_EVENT);

        verify(getMainTimelineInteractor).loadMainTimeline(any(GetMainTimelineInteractor.Callback.class));
    }

    @Test
    public void shouldShotSentReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = ShotSent.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod = shotSentReceiver.getClass().getMethod(receiverMethodName, ShotSent.Event.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    @Test
    public void shouldEventChangedReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = EventChanged.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod = eventChangedReceiver.getClass().getMethod(receiverMethodName, EventChanged.Event.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    private void setupGetOlderTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetOlderMainTimelineInteractor.Callback) invocation.getArguments()[1]).onLoaded(timeline);
                return null;
            }
        }).when(getOlderMainTimelineInteractor).loadOlderMainTimeline(anyLong(),
          any(GetOlderMainTimelineInteractor.Callback.class));
    }

    private ShotModel lastShotModel() {
        ShotModel shotModel = new ShotModel();
        shotModel.setCsysBirth(LAST_SHOT_DATE);
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

    private void setupGetMainTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                ((GetMainTimelineInteractor.Callback) invocation.getArguments()[0]).onLoaded(timeline);
                return null;
            }
        }).when(getMainTimelineInteractor).loadMainTimeline(any(GetMainTimelineInteractor.Callback.class));
    }

    private void setupRefreshMainTimelineInteractorCallbacks(final Timeline timeline) {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                ((RefreshMainTimelineInteractor.Callback) invocation.getArguments()[0]).onLoaded(timeline);
                return null;
            }
        }).when(refreshMainTimelineInteractor).refreshMainTimeline(any(RefreshMainTimelineInteractor.Callback.class));
    }
}