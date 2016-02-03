package com.shootr.mobile.ui.presenter;

import android.support.annotation.NonNull;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserFollowersInteractor;
import com.shootr.mobile.domain.interactor.user.GetUserFollowingInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.UserModel;
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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
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
    @Mock GetUserFollowersInteractor getUserFollowersInteractor;
    @Mock FollowInteractor followInteractor;
    @Mock UnfollowInteractor unfollowInteractor;

    private UserFollowsPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        presenter = new UserFollowsPresenter(getUserFollowingInteractor,
          getUserFollowersInteractor, followInteractor, unfollowInteractor, errorMessageFactory, userModelMapper);
        presenter.setView(userFollowsView);
    }

    @Test public void shouldCallGetFollowingInteractorIfObtainingFollowingUsers() throws Exception {
        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(getUserFollowingInteractor).obtainFollowing(anyString(),
          anyInt(),
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

    @Test public void shouldShowLoadingWhenPresenterInitializedSearchingFollowing() throws Exception {
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

    @Test public void shouldShowNoFollowingIfTheresNoFollowing() throws Exception {
        setupEmptyGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);

        verify(userFollowsView).showNoFollowing();
    }

    @Test public void shouldCallGetFollowersInteractorIfObtainingFollowingUsers() throws Exception {
        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);

        verify(getUserFollowersInteractor).obtainFollowers(anyString(),
          anyInt(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldShowErrorInViewIfGetFollowersInteractorThrowsServerCommunicationException()
      throws Exception {
        setupGetFollowersInteractorErrorCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);

        verify(userFollowsView).showError(anyString());
    }

    @Test public void shouldShowFollowersUsersInViewWhenObtainingFollowingUsers() throws Exception {
        setupGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);

        verify(userFollowsView).showUsers(any(List.class));
    }

    @Test public void shouldShowLoadingWhenPresenterInitializedSearchingFollowers() throws Exception {
        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);

        verify(userFollowsView).setLoadingView(true);
    }

    @Test public void shouldHideLoadingWhenFollowersUsersShown() throws Exception {
        setupGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);

        verify(userFollowsView).setLoadingView(false);
    }

    @Test public void shouldHideLoadingWhenFollowersUsersThrowsError() throws Exception {
        setupGetFollowersInteractorErrorCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);

        verify(userFollowsView).setLoadingView(false);
    }

    @Test public void shouldShowEmptyIfTheresNoFollowers() throws Exception {
        setupEmptyGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);

        verify(userFollowsView).setEmpty(true);
    }

    @Test public void shouldShowNoFollowersIfTheresNoFollowers() throws Exception {
        setupEmptyGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);

        verify(userFollowsView).showNoFollowers();
    }

    @Test public void shouldUpdateFollowWhenClickOnFollowUser() throws Exception {
        setupFollowCompletedCallback();

        presenter.follow(userModel());

        verify(userFollowsView).updateFollow(anyString(), anyBoolean());
    }

    @Test public void shouldShowBlockedErrorWhenClickOnFollowUserAndErrorThrown() throws Exception {
        setupFollowErrorCallback();

        presenter.follow(userModel());

        verify(userFollowsView).showUserBlockedError();
    }

    @Test public void shouldUpdateFollowWhenClickOnUnfollowUser() throws Exception {
        setupUnfollowCompletedCallback();

        presenter.unfollow(userModel());

        verify(userFollowsView).updateFollow(anyString(), anyBoolean());
    }

    @Test public void shouldShowLoadingWhenLoadingMoreFollowings() throws Exception {
        setupGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).showProgressView();
    }

    @Test public void shouldShowLoadingWhenLoadingMoreFollowers() throws Exception {
        setupGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).showProgressView();
    }

    @Test public void shouldShowUsersWhenLoadingMoreFollowings() throws Exception {
        setupGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).showUsers(any(List.class));
    }

    @Test public void shouldShowUsersWhenLoadingMoreFollowers() throws Exception {
        setupGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).showUsers(any(List.class));
    }

    @Test public void shouldShowEmptyWhenLoadingMoreFollowingsReturnsEmpty() throws Exception {
        setupEmptyGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).setEmpty(true);
    }

    @Test public void shouldShowEmptyWhenLoadingMoreFollowersReturnsEmpty() throws Exception {
        setupEmptyGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).setEmpty(true);
    }

    @Test public void shouldHideLoadingWhenLoadingMoreFollowings() throws Exception {
        setupGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).hideProgressView();
    }

    @Test public void shouldHideLoadingWhenLoadingMoreFollowers() throws Exception {
        setupGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).hideProgressView();
    }

    @Test public void shouldHideLoadingWhenLoadingMoreFollowingsReturnsEmpty() throws Exception {
        setupEmptyGetFollowingUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWING);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).hideProgressView();
    }

    @Test public void shouldHideLoadingWhenLoadingMoreFollowersReturnsEmpty() throws Exception {
        setupEmptyGetFollowersUsersCallback();

        presenter.initialize(userFollowsView, USER_ID, FOLLOWERS);
        presenter.makeNextRemoteSearch();

        verify(userFollowsView).hideProgressView();
    }

    public void setupUnfollowCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(unfollowInteractor).unfollow(anyString(), any(Interactor.CompletedCallback.class));
    }

    public void setupFollowErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback =
                  (Interactor.ErrorCallback) invocation.getArguments()[2];
                callback.onError(new ShootrException() {});
                return null;
            }
        }).when(followInteractor).follow(anyString(),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    public void setupFollowCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(followInteractor).follow(anyString(),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupEmptyGetFollowersUsersCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[2];
                callback.onLoaded(new ArrayList<User>());
                return null;
            }
        }).when(getUserFollowersInteractor).obtainFollowers(anyString(), anyInt(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupGetFollowersUsersCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[2];
                callback.onLoaded(users());
                return null;
            }
        }).when(getUserFollowersInteractor).obtainFollowers(anyString(), anyInt(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupGetFollowersInteractorErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback =
                  (Interactor.ErrorCallback) invocation.getArguments()[3];
                errorCallback.onError(any(ShootrException.class));
                return null;
            }
        }).when(getUserFollowersInteractor).obtainFollowers(anyString(), anyInt(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupEmptyGetFollowingUsersCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[2];
                callback.onLoaded(new ArrayList<User>());
                return null;
            }
        }).when(getUserFollowingInteractor).obtainFollowing(anyString(), anyInt(), any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    public void setupGetFollowingInteractorErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback =
                  (Interactor.ErrorCallback) invocation.getArguments()[3];
                errorCallback.onError(any(ShootrException.class));
                return null;
            }
        }).when(getUserFollowingInteractor).obtainFollowing(anyString(), anyInt(), any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    public void setupGetFollowingUsersCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[2];
                callback.onLoaded(users());
                return null;
            }
        }).when(getUserFollowingInteractor).obtainFollowing(anyString(), anyInt(), any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private List<User> users() {
        List<User> users = new ArrayList<>();
        User user = user();
        users.add(user);
        return users;
    }

    @NonNull private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        return user;
    }

    private UserModel userModel() {
        UserModel user = new UserModel();
        user.setIdUser(ID_USER);
        return user;
    }
}