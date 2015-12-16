package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.GetUserFollowingInteractor;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.UserFollowsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class UserFollowsPresenterTest {

    public static final String USER_ID = "userId";
    public static final int FOLLOWING = 1;
    public static final int FOLLOWERS = 0;
    public static final String ID_USER = "idUser";

    @Mock UserFollowsView userFollowsView;
    @Mock GetUserFollowingInteractor getUserFollowingInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock StreamJoinDateFormatter streamJoinDateFormatter;
    private UserFollowsPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        presenter = new UserFollowsPresenter(getUserFollowingInteractor, errorMessageFactory, userModelMapper);
        presenter.setView(userFollowsView);
    }

    @Test public void shouldCallGetFollowingInteractorIfObtainingFollowingUsers() throws Exception {
        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(getUserFollowingInteractor).obtainFollowing(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldShowErrorInViewIfGetFollowingInteractorThrowsServerCommunicationException()
      throws Exception {
        setupGetFollowingInteractorErrorCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(userFollowsView).showError(anyString());
    }

    @Test public void shouldShowFollowingUsersInViewWhenObtainingFollowingUsers() throws Exception {
        setupGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(userFollowsView).showUsers(any(List.class));
    }

    @Test public void shouldShowLoadingWhenPresenterInitialized() throws Exception {
        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(userFollowsView).setLoadingView(true);
    }

    @Test public void shouldHideLoadingWhenFollowingUsersShown() throws Exception {
        setupGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(userFollowsView).setLoadingView(false);
    }

    @Test public void shouldHideLoadingWhenFollowingUsersThrowsError() throws Exception {
        setupGetFollowingInteractorErrorCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(userFollowsView).setLoadingView(false);
    }

    @Test public void shouldShowEmptyIfTheresNoFollowing() throws Exception {
        setupEmptyGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(userFollowsView).setEmpty(true);
    }

    private void setupEmptyGetFollowingUsersCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[1];
                callback.onLoaded(new ArrayList<User>());
                return null;
            }
        }).when(getUserFollowingInteractor).obtainFollowing(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    public void setupGetFollowingInteractorErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback =
                  (Interactor.ErrorCallback) invocation.getArguments()[2];
                errorCallback.onError(any(ShootrException.class));
                return null;
            }
        }).when(getUserFollowingInteractor).obtainFollowing(anyString(), any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    public void setupGetFollowingUsersCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[1];
                callback.onLoaded(users());
                return null;
            }
        }).when(getUserFollowingInteractor).obtainFollowing(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private List<User> users() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setIdUser(ID_USER);
        users.add(user);
        return users;
    }
}
