package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.prefs.IntPreference;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.discover.SendDeviceInfoInteractor;
import com.shootr.mobile.domain.interactor.shot.SendShotEventStatsIneteractor;
import com.shootr.mobile.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.MainScreenView;
import com.squareup.otto.Bus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainScreenPresenterTest {

    public static final int TWO_ACTIVITIES = 2;
    private static final int ONE_ACTIVITY = 1;
    private static final int ZERO_ACTIVITY = 0;
    public static final String ID_USER = "idUser";
    @Mock DateRangeTextProvider dateRangeTextProvider;
    @Mock TimeUtils timeUtils;
    @Mock GetCurrentUserInteractor getCurrentUserInteractor;
    @Mock SendDeviceInfoInteractor sendDeviceInfoInteractor;
    @Mock SendShotEventStatsIneteractor sendShoEventStatsIneteractor;
    @Mock IntPreference badgeCount;
    @Mock Bus bus;
    @Mock MainScreenView view;
    private MainScreenPresenter mainScreenPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper =
          new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
        mainScreenPresenter =
          new MainScreenPresenter(getCurrentUserInteractor, sendDeviceInfoInteractor,
              sendShoEventStatsIneteractor, userModelMapper, badgeCount, bus);
        mainScreenPresenter.setView(view);
    }

    @Test public void shouldSetUserDataWhenLoadCurrentUserOnInitialize() throws Exception {
        setupCurrentUserCallback();

        mainScreenPresenter.initialize(view);

        verify(view).setUserData(any(UserModel.class));
    }

    @Test public void shouldSetUserDataWhenLoadCurrentUserOnResume() throws Exception {
        setupCurrentUserCallback();

        mainScreenPresenter.pause();
        mainScreenPresenter.resume();

        verify(view).setUserData(any(UserModel.class));
    }

    @Test public void shouldSendDeviceInfo() throws Exception {
        mainScreenPresenter.initialize(view);

        verify(sendDeviceInfoInteractor).sendDeviceInfo();
    }

    @Test public void shouldUpdateActivityBadge() throws Exception {
        setupNumberOfNewActivities(1);

        mainScreenPresenter.initialize(view);

        verify(view).showActivityBadge(anyInt());
    }

    @Test public void shouldNotUpdateActivityBadgeWhenNegativeValue() throws Exception {
        setupNumberOfNewActivities(-1);

        mainScreenPresenter.initialize(view);

        verify(view, never()).showActivityBadge(anyInt());
    }

    @Test public void shouldNotUpdateActivityBadgeWhenZeroValue() throws Exception {
        setupNumberOfNewActivities(ZERO_ACTIVITY);

        mainScreenPresenter.initialize(view);

        verify(view, never()).showActivityBadge(anyInt());
    }

    private void setupCurrentUserCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<User> callback = (Interactor.Callback<User>) invocation.getArguments()[0];
                callback.onLoaded(user());
                return null;
            }
        }).when(getCurrentUserInteractor).getCurrentUser(any(Interactor.Callback.class));
    }

    private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        return user;
    }

    private void setupNumberOfNewActivities(Integer numberOfActivities) {
        when(badgeCount.get()).thenReturn(numberOfActivities);
    }
}
