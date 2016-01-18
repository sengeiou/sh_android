package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.interactor.SendDeviceInfoInteractor;
import com.shootr.mobile.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.MainScreenView;
import com.squareup.otto.Bus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainScreenPresenterTest {

    public static final int TWO_ACTIVITIES = 2;
    private static final int ONE_ACTIVITY =1 ;
    private static final int ZERO_ACTIVITY =0 ;
    @Mock DateRangeTextProvider dateRangeTextProvider;
    @Mock TimeUtils timeUtils;
    @Mock GetCurrentUserInteractor getCurrentUserInteractor;
    @Mock SendDeviceInfoInteractor sendDeviceInfoInteractor;
    @Mock IntPreference badgeCount;
    @Mock Bus bus;
    @Mock MainScreenView view;
    private MainScreenPresenter mainScreenPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper =
          new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
        mainScreenPresenter =
          new MainScreenPresenter(getCurrentUserInteractor, sendDeviceInfoInteractor, userModelMapper, badgeCount, bus);
    }

    @Test public void shouldShowMultipleActivitiesWhenActivitiesAreMoreThanOne() throws Exception {
        when(badgeCount.get()).thenReturn(TWO_ACTIVITIES);

        mainScreenPresenter.initialize(view);

        verify(view).showHasMultipleActivities(TWO_ACTIVITIES);
    }

    @Test public void shouldNotShowMultipleActivitiesWhenOnlyOneActivity() throws Exception {
        when(badgeCount.get()).thenReturn(ONE_ACTIVITY);

        mainScreenPresenter.initialize(view);

        verify(view, never()).showHasMultipleActivities(anyInt());
    }

    @Test public void shouldNotShowMultipleActivitiesWhenZeroActivities() throws Exception {
        when(badgeCount.get()).thenReturn(ZERO_ACTIVITY);

        mainScreenPresenter.initialize(view);

        verify(view,never()).showHasMultipleActivities(anyInt());
    }
}
