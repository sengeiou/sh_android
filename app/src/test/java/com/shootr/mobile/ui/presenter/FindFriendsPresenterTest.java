package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FindFriendsInteractor;
import com.shootr.mobile.domain.interactor.user.FindFriendsServerInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.FindFriendsView;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class FindFriendsPresenterTest {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String STREAM = "streamId";
    private static final String QUERY = "query";
    @Mock FindFriendsInteractor findFriendsInteractor;
    @Mock FollowInteractor followInteractor;
    @Mock UnfollowInteractor unfollowInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock StreamJoinDateFormatter streamJoinDateFormatter;
    @Mock FindFriendsView findFriendsView;
    @Mock FindFriendsServerInteractor findFriendsServerInteractor;

    private FindFriendsPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        presenter = new FindFriendsPresenter(findFriendsInteractor,
          findFriendsServerInteractor,
          followInteractor,
          unfollowInteractor,
          userModelMapper,
          errorMessageFactory);
        presenter.setView(findFriendsView);
    }

    @Test public void shouldShowContentWhenSearchFriendsAndFriendsIsNotEmpty() throws Exception {
        setupFindFriendsInteractorWithUsers();
        presenter.initialize(findFriendsView);

        presenter.searchFriends(QUERY);

        verify(findFriendsView).showContent();
    }

    @Test public void shouldNotShowContentWhenSearchFriendsAndFriendsIsEmpty() throws Exception {
        setupFindFriendsInteractorWithoutUsers();
        presenter.initialize(findFriendsView);

        presenter.searchFriends(QUERY);

        verify(findFriendsView, never()).showContent();
    }

    @Test public void shouldRenderFriendsWhenSearchFriendsAndFriendsIsNotEmpty() throws Exception {
        setupFindFriendsInteractorWithUsers();
        presenter.initialize(findFriendsView);

        presenter.searchFriends(QUERY);

        verify(findFriendsView).renderFriends(anyList());
    }

    @Test public void shouldNotRenderFriendsWhenSearchFriendsAndFriendsIsEmpty() throws Exception {
        setupFindFriendsInteractorWithoutUsers();
        presenter.initialize(findFriendsView);

        presenter.searchFriends(QUERY);

        verify(findFriendsView, never()).renderFriends(anyList());
    }

    @Test public void shouldShowEmptyWhenSearFriendsAndFriendsIsEmpty() throws Exception {
        setupFindFriendsInteractorWithoutUsers();
        presenter.initialize(findFriendsView);

        presenter.searchFriends(QUERY);

        verify(findFriendsView).showEmpty();
    }

    @Test public void shouldHideEmptyWhenRefreshFriends() throws Exception {
        setupFindFriendsInteractorWithoutUsers();
        presenter.initialize(findFriendsView);

        presenter.refreshFriends();

        verify(findFriendsView).hideEmpty();
    }

    @Test public void shouldNotRenderFriendsWhenRefreshAndFriendsIsEmpty() throws Exception {
        setupFindFriendsInteractorWithoutUsers();
        presenter.initialize(findFriendsView);

        presenter.refreshFriends();

        verify(findFriendsView, never()).renderFriends(anyList());
    }

    @Test public void shouldRenderFriendsWhenRefreshAndFriendsIsNotEmpty() throws Exception {
        setupFindFriendsInteractorWithUsers();
        presenter.initialize(findFriendsView);

        presenter.refreshFriends();

        verify(findFriendsView).renderFriends(anyList());
    }

    @Test public void shouldRenderFriendWhenFollowUser() throws Exception {
        setupFollowInteractor();
        setupFindFriendsInteractorWithUsers();
        presenter.initialize(findFriendsView);
        presenter.searchFriends(QUERY);

        presenter.followUser(userModel());

        verify(findFriendsView, atLeast(1)).renderFriends(anyList());
    }

    @Test public void shouldRenderFriendWhenUnFollowUser() throws Exception {
        setupUnFollowInteractor();
        setupFindFriendsInteractorWithUsers();
        presenter.initialize(findFriendsView);
        presenter.searchFriends(QUERY);

        presenter.followUser(userModel());

        verify(findFriendsView, atLeast(1)).renderFriends(anyList());
    }

    @Test public void shouldSearchFriendsWhenHaveBeenPausedAndIsInOnResume() throws Exception {
        setupFindFriendsInteractorWithUsers();
        presenter.pause();
        presenter.resume();

        presenter.searchFriends(QUERY);

        verify(findFriendsView).renderFriends(anyList());
    }

    @Test public void shouldAddFriendsWhenMakeTheNextRemoteSearch() throws Exception {
        setupFindFriendsServerInteractorWithUsers();
        presenter.initialize(findFriendsView);

        presenter.makeNextRemoteSearch();

        verify(findFriendsView).addFriends(anyList());
    }
    
    private void setupFollowInteractor() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (FollowInteractor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(followInteractor)
          .follow(anyString(), any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupUnFollowInteractor() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (UnfollowInteractor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(unfollowInteractor).unfollow(anyString(), any(Interactor.CompletedCallback.class));
    }

    private void setupFindFriendsInteractorWithUsers() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                FindFriendsInteractor.Callback callback = (FindFriendsInteractor.Callback) invocation.getArguments()[2];
                callback.onLoaded(users());
                return null;
            }
        }).when(findFriendsInteractor)
          .findFriends(anyString(), anyInt(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupFindFriendsInteractorWithoutUsers() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                FindFriendsInteractor.Callback callback = (FindFriendsInteractor.Callback) invocation.getArguments()[2];
                callback.onLoaded(emptyUsers());
                return null;
            }
        }).when(findFriendsInteractor)
          .findFriends(anyString(), anyInt(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupFindFriendsServerInteractorWithUsers() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                FindFriendsServerInteractor.Callback callback = (FindFriendsServerInteractor.Callback) invocation.getArguments()[2];
                callback.onLoaded(users());
                return null;
            }
        }).when(findFriendsServerInteractor)
          .findFriends(anyString(), anyInt(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private List<User> emptyUsers() {
        return new ArrayList<>();
    }

    private List<User> users() {
        ArrayList<User> users = new ArrayList<>();
        users.add(user());
        return users;
    }

    private User user() {
        User user = new User();
        user.setIdUser(USER_ID);
        user.setUsername(USERNAME);
        user.setIdWatchingStream(STREAM);
        return user;
    }

    private UserModel userModel() {
        UserModel userModel = new UserModel();
        userModel.setIdUser(USER_ID);
        userModel.setUsername(USERNAME);
        userModel.setStreamWatchingId(STREAM);
        return userModel;
    }
}