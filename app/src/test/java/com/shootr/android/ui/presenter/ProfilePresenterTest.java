package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetLastShotsInteractor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.ShareShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.domain.interactor.user.FollowInteractor;
import com.shootr.android.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.android.domain.interactor.user.GetUserByUsernameInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.domain.interactor.user.UnfollowInteractor;
import com.shootr.android.domain.utils.StreamJoinDateFormatter;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.ErrorMessageFactory;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ProfilePresenterTest {

    public static final String ID_USER = "id_user";
    public static final String SELECTED_STREAM_ID = "selected_stream_id";
    public static final String USERNAME = "username";

    @Mock LogoutInteractor logoutInteractor;
    @Mock ProfileView profileView;
    @Mock MarkNiceShotInteractor markNiceShotInteractor;
    @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    @Mock ShareShotInteractor shareShotInteractor;
    @Mock FollowInteractor followInteractor;
    @Mock UnfollowInteractor unfollowInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock GetUserByUsernameInteractor getUserByUsernameInteractor;
    @Mock GetUserByIdInteractor getUserByIdInteractor;
    @Mock StreamJoinDateFormatter streamJoinDateFormatter;
    @Mock GetLastShotsInteractor getLastShotsInteractor;

    private ProfilePresenter profilePresenter;
    private UserModelMapper userModelMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        profilePresenter = new ProfilePresenter(getUserByIdInteractor, getUserByUsernameInteractor, logoutInteractor,
          markNiceShotInteractor,
          unmarkNiceShotInteractor,
          shareShotInteractor,
          followInteractor,
          unfollowInteractor, getLastShotsInteractor, errorMessageFactory, userModelMapper);
        profilePresenter.setView(profileView);
    }

    @Test public void shouldGetUserByIdIfItHasBeenInitializedWithUserId() throws Exception {
        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(getUserByIdInteractor).loadUserById(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));

    }

    @Test public void shouldGetUserByYsernameIfItHasBeenInitializedWithUsername() throws Exception {
        profilePresenter.initializeWithUsername(profileView, USERNAME);

        verify(getUserByUsernameInteractor).searchUserByUsername(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldSetUserInfoWhenUserHasBeenInitializedWithUserId() throws Exception {
        setupUserById();

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).setUserInfo(any(UserModel.class));

    }

    @Test public void shouldSetUserInfoWhenUserHasBeenInitializedWithUsername() throws Exception {
        setupUserByUsername();

        profilePresenter.initializeWithUsername(profileView, USERNAME);

        verify(profileView).setUserInfo(any(UserModel.class));

    }

    @Test
    public void shouldShowListingButtonWithCountWhenPresenterInitializedWithIdUserAndUserHasStreams() {
        setupUserById();

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showListingButtonWithCount(anyInt());
    }

    @Test
    public void shouldShowListingButtonWithCountWhenPresenterInitializedWithUsernameAndUserHasStreams() {
        setupUserByUsername();

        profilePresenter.initializeWithUsername(profileView, USERNAME);

        verify(profileView).showListingButtonWithCount(anyInt());
    }

    @Test
    public void shouldNotShowOpenStreamWhenInOtherUsersProfileWithoutCount() {
        User userWithoutListing = userWithCounts(0, 0);
        setupUserIdInteractorCallbacks(userWithoutListing);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView, never()).showOpenStream();
    }

    @Test
    public void shouldShowOpenStreamWhenInCurrentUsersProfileWithoutCount() {
        User userWithoutListing = userWithCounts(0, 0);
        userWithoutListing.setMe(true);
        setupUserIdInteractorCallbacks(userWithoutListing);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showOpenStream();
    }

    @Test public void shouldShowAllShotsIfUserHaveFourLatsShots() throws Exception {
        setupUserById();
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(shotList(4));
                return null;
            }
        }).when(getLastShotsInteractor).loadLastShots(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showAllShots();
    }

    @Test public void shouldHideAllShotsIfUserHaveThreeLatestShots() throws Exception {
        setupUserById();
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(shotList(3));
                return null;
            }
        }).when(getLastShotsInteractor).loadLastShots(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).hideAllShots();
    }

    @Test public void shouldShowEditButtonWhenCurrentUserProfile() throws Exception {
        User user = user();
        user.setMe(true);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showEditProfileButton();
    }

    @Test public void shouldShowFollowButtonWhenNotFollowingUser() throws Exception {
        User user = user();
        user.setMe(false);
        user.setFollowing(false);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showFollowButton();
    }

    @Test public void shouldShowUnfollowButtonWhenFollowingUser() throws Exception {
        User user = user();
        user.setFollowing(true);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showUnfollowButton();
    }

    @Test public void shouldShowAddPhotoIfCurrentUserHasNoPhoto() throws Exception {
        User user = user();
        user.setMe(true);
        user.setPhoto(null);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showAddPhoto();
    }

    @Test public void shouldNotShowAddPhotoIfAnotherUserHasNoPhoto() throws Exception {
        User user = user();
        user.setPhoto(null);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView, never()).showAddPhoto();
    }

    @Test public void shouldOpenPhotoWhenAvatarClickedAndNotCurrentUser() throws Exception {
        setupUserIdInteractorCallbacks(user());

        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        profilePresenter.avatarClicked();

        verify(profileView).openPhoto(anyString());
    }

    @Test public void shouldOpenEditPhotoMenuWhenAvatarClickedAndCurrentUser() throws Exception {
        User user = user();
        user.setMe(true);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        profilePresenter.avatarClicked();

        verify(profileView).openEditPhotoMenu(anyBoolean());
    }

    @Test public void shouldShowRemoveInPhotoMenuWhenAvatarClickedAndCurrentUserHasPhoto() throws Exception {
        User user = user();
        user.setMe(true);
        user.setPhoto("photo");
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        profilePresenter.avatarClicked();

        verify(profileView).openEditPhotoMenu(true);
    }

    @Test public void shouldNotShowRemoveInPhotoMenuWhenAvatarClickedAndCurrentUserHasPhoto() throws Exception {
        User user = user();
        user.setMe(true);
        user.setPhoto(null);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        profilePresenter.avatarClicked();

        verify(profileView).openEditPhotoMenu(false);
    }

    @Test
    public void shouldShowLogoutInProgressWhenLogoutSelected() {
        profilePresenter.logoutSelected();

        verify(profileView).showLogoutInProgress();
    }

    @Test
    public void shouldCallbackLogoutInteractorWhenLogoutSelected() {
        profilePresenter.logoutSelected();

        verify(logoutInteractor).attempLogout(any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test
    public void shouldNavigateToWelcomeScreenWhenLogoutSelectedAndCompletedCallback() {
        setupLogoutInteractorCompletedCallback();

        profilePresenter.logoutSelected();

        verify(profileView).navigateToWelcomeScreen();
    }

    @Test
    public void shouldHideLogoutInProgressWhenLogoutSelectedAndErrorCallback() {
        setupLogoutInteractorErrorCallback();

        profilePresenter.logoutSelected();

        verify(profileView).hideLogoutInProgress();
    }

    @Test
    public void shouldShowErrorWhenLogoutSelectedAndErrorCallback() {
        setupLogoutInteractorErrorCallback();

        profilePresenter.logoutSelected();

        verify(profileView).showError();
    }

    @Test
    public void shouldShowLogoutButtonWhenUserIsCurrentUser() {
        User user = user();
        user.setMe(true);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showLogoutButton();
    }

    @Test public void shouldNavigateToStreamDetailWhenNewStreamCreated() throws Exception {
        profilePresenter.streamCreated(SELECTED_STREAM_ID);

        verify(profileView).navigateToCreatedStreamDetail(SELECTED_STREAM_ID);
    }

    @Test public void shouldNavigateToStreamDetailWhenNewStreamCreatedIfSelectStreamInteractorCallbacksStreamId() throws Exception {
        profilePresenter.streamCreated(SELECTED_STREAM_ID);

        verify(profileView).navigateToCreatedStreamDetail(SELECTED_STREAM_ID);
    }

    private void setupLogoutInteractorCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback = (Interactor.CompletedCallback) invocation.getArguments()[0];
                completedCallback.onCompleted();
                return null;
            }
        }).when(logoutInteractor)
          .attempLogout(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupLogoutInteractorErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[1];
                errorCallback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(logoutInteractor)
          .attempLogout(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        user.setUsername(USERNAME);
        user.setCreatedStreamsCount(1L);
        user.setFavoritedStreamsCount(1L);
        return user;
    }

    private void setupUserByUsername() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(user());
                return null;
            }
        }).when(getUserByUsernameInteractor)
          .searchUserByUsername(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupUserById() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(user());
                return null;
            }
        }).when(getUserByIdInteractor)
          .loadUserById(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private List<Shot> shotList(int numberOfShots) {
        List<Shot> shots = new ArrayList<>();
        for(int i = 0; i<numberOfShots;i++) {
            Shot shot = new Shot();
            shot.setIdShot("idShot"+i);
            shots.add(shot);
        }
        return shots;
    }

    private void setupUserIdInteractorCallbacks(final User user) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback<User>) invocation.getArguments()[1];
                callback.onLoaded(user);
                return null;
            }
        }).when(getUserByIdInteractor)
          .loadUserById(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));

    }

    private User userWithCounts(int createdCount, int favoritedCount) {
        User user = user();
        user.setCreatedStreamsCount((long) createdCount);
        user.setFavoritedStreamsCount((long) favoritedCount);
        return user;
    }
}
