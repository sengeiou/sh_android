package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.Poller;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.model.mappers.ActivityModelMapper;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.presenter.interactorwrapper.ActivityTimelineInteractorsWrapper;
import com.shootr.mobile.ui.views.GenericActivityTimelineView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class GenericActivityTimelinePresenterTest {

    public static final boolean NOT_USER_ACTIVITY_TIMELINE = false;
    private GenericActivityTimelinePresenter genericActivityTimelinePresenter;
    @Mock ActivityTimelineInteractorsWrapper activityTimelineInteractorWrapper;
    @Mock Bus bus;
    @Mock Poller poller;
    @Mock IntPreference badgeCount;
    @Mock ShotModelMapper shotModelMapper;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;
    @Mock GenericActivityTimelineView view;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ActivityModelMapper activityModelMapper = new ActivityModelMapper(shotModelMapper);
        genericActivityTimelinePresenter = new GenericActivityTimelinePresenter(activityTimelineInteractorWrapper,
          activityModelMapper,
          bus,
          errorMessageFactory,
          poller,
          badgeCount,
          sessionRepository);
        genericActivityTimelinePresenter.setView(view);
    }

    @Test public void shouldSetActivitiesInViewWhenInitializeAllActivitiesAndTimelineNotEmptyCallback()
      throws Exception {
        setupLoadTimelineCallback();

        genericActivityTimelinePresenter.initialize(view, NOT_USER_ACTIVITY_TIMELINE);

        verify(view).setActivities(anyList(), anyString());
    }

    @Test public void shouldHideEmptyWhenInitializeAllActivitiesAndTimelineNotEmptyCallback() throws Exception {
        setupLoadTimelineCallback();

        genericActivityTimelinePresenter.initialize(view, NOT_USER_ACTIVITY_TIMELINE);

        verify(view).hideEmpty();
    }

    @Test public void shouldShowActivitiesWhenInitializeAllActivitiesAndTimelineNotEmptyCallback() throws Exception {
        setupLoadTimelineCallback();

        genericActivityTimelinePresenter.initialize(view, NOT_USER_ACTIVITY_TIMELINE);

        verify(view).showActivities();
    }

    @Test public void shouldShowEmptyWhenInitializeAllActivitiesAndTimelineEmptyCallback() throws Exception {
        setupLoadEmptyTimelineCallback();

        genericActivityTimelinePresenter.initialize(view, NOT_USER_ACTIVITY_TIMELINE);

        verify(view).showEmpty();
    }

    @Test public void shouldHideActivitiesWhenInitializeAllActivitiesAndTimelineEmptyCallback() throws Exception {
        setupLoadEmptyTimelineCallback();

        genericActivityTimelinePresenter.initialize(view, NOT_USER_ACTIVITY_TIMELINE);

        verify(view).hideActivities();
    }

    @Test public void shouldShowLoadingWhenRefresh() throws Exception {
        genericActivityTimelinePresenter.refresh();

        verify(view).showLoading();
    }

    @Test public void shouldHideLoadingWhenRefresh() throws Exception {
        setupRefreshTimelineCallback();

        genericActivityTimelinePresenter.refresh();

        verify(view).hideEmpty();
    }

    @Test public void shouldAddNewActivitiesWhenRefresh() throws Exception {
        setupRefreshTimelineCallback();

        genericActivityTimelinePresenter.refresh();

        verify(view).addNewActivities(anyList());
    }

    @Test public void shouldShowActivitiesNewActivitiesWhenRefresh() throws Exception {
        setupRefreshTimelineCallback();

        genericActivityTimelinePresenter.refresh();

        verify(view).showActivities();
    }

    @Test public void shouldHideLoadingWhenRefreshAndRefreshTimelineCallback() throws Exception {
        setupRefreshTimelineCallback();

        genericActivityTimelinePresenter.refresh();

        verify(view).hideLoading();
    }

    @Test public void shouldHideLoadingActivitiyWhenRefreshAndRefreshTimelineCallback() throws Exception {
        setupRefreshTimelineCallback();

        genericActivityTimelinePresenter.refresh();

        verify(view).hideLoadingActivity();
    }

    @Test public void shouldAddNewActivitiesWhenRefreshTimelineCallback() throws Exception {
        setupRefreshTimelineCallback();

        genericActivityTimelinePresenter.refresh();

        verify(view).hideLoadingActivity();
    }

    @Test public void shouldHideLoadingOldActivitiesWhenObtainOlderActivitiesTimelineCallback() throws Exception {
        setupOlderTimelineCallback();

        genericActivityTimelinePresenter.showingLastActivity(activityModel());

        verify(view).hideLoadingOldActivities();
    }

    @Test public void shouldAddOldActivitiesWhenObtainOlderActivitiesTimelineCallback() throws Exception {
        setupOlderTimelineCallback();

        genericActivityTimelinePresenter.showingLastActivity(activityModel());

        verify(view).addOldActivities(anyList());
    }

    private void setupOlderTimelineCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ActivityTimeline> callback =
                  (Interactor.Callback<ActivityTimeline>) invocation.getArguments()[2];
                callback.onLoaded(activityTimeline());
                return null;
            }
        }).when(activityTimelineInteractorWrapper)
          .obtainOlderTimeline(anyBoolean(),
            anyLong(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private ActivityModel activityModel() {
        ActivityModel activityModel = new ActivityModel();
        activityModel.setPublishDate(new Date());
        return activityModel;
    }

    private void setupRefreshTimelineCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ActivityTimeline> callback =
                  (Interactor.Callback<ActivityTimeline>) invocation.getArguments()[1];
                callback.onLoaded(activityTimeline());
                return null;
            }
        }).when(activityTimelineInteractorWrapper)
          .refreshTimeline(anyBoolean(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupLoadTimelineCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ActivityTimeline> callback =
                  (Interactor.Callback<ActivityTimeline>) invocation.getArguments()[1];
                callback.onLoaded(activityTimeline());
                return null;
            }
        }).when(activityTimelineInteractorWrapper).loadTimeline(anyBoolean(), any(Interactor.Callback.class));
    }

    private void setupLoadEmptyTimelineCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ActivityTimeline> callback =
                  (Interactor.Callback<ActivityTimeline>) invocation.getArguments()[1];
                callback.onLoaded(emptyTimeline());
                return null;
            }
        }).when(activityTimelineInteractorWrapper).loadTimeline(anyBoolean(), any(Interactor.Callback.class));
    }

    private ActivityTimeline emptyTimeline() {
        ActivityTimeline timeline = new ActivityTimeline();
        timeline.setActivities(Collections.<Activity>emptyList());
        return timeline;
    }

    private ActivityTimeline activityTimeline() {
        ActivityTimeline timeline = new ActivityTimeline();
        timeline.setActivities(activities());
        return timeline;
    }

    private List<Activity> activities() {
        Activity activity = new Activity();
        activity.setComment("comment");
        activity.setIdActivity("idActivity");
        activity.setIdAuthorStream("idAuthor");
        activity.setPublishDate(new Date());
        activity.setStreamInfo(new Activity.ActivityStreamInfo());
        activity.setUserInfo(new Activity.ActivityUserInfo());
        return Collections.singletonList(activity);
    }
}
