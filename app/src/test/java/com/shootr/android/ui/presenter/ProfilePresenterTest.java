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
import com.shootr.android.domain.interactor.user.RemoveUserPhotoInteractor;
import com.shootr.android.domain.interactor.user.UnfollowInteractor;
import com.shootr.android.domain.interactor.user.UploadUserPhotoInteractor;
import com.shootr.android.domain.utils.StreamJoinDateFormatter;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.ErrorMessageFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class ProfilePresenterTest {

    public static final String ID_USER = "id_user";
    public static final String SELECTED_STREAM_ID = "selected_stream_id";
    public static final String USERNAME = "username";
    public static final String WEBSITE_NO_PREFIX = "website";
    public static final String HTTP_PREFIX = "http://";
    private static final String WEBSITE_HTTP_PREFIX = "http://website";
    private static final String WEBSITE_HTTPS_PREFIX = "https://website";
    public static final String PHOTO_PATH = "photoPath";

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
    @Mock UploadUserPhotoInteractor uploadUserPhotoInteractor;
    @Mock RemoveUserPhotoInteractor removeUserPhotoInteractor;

    @Captor ArgumentCaptor<List<ShotModel>> shotModelListCaptor;

    private ProfilePresenter profilePresenter;
    private UserModelMapper userModelMapper;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        ShotModelMapper shotModelMapper = new ShotModelMapper();
        profilePresenter = new ProfilePresenter(getUserByIdInteractor,
          getUserByUsernameInteractor,
          logoutInteractor,
          markNiceShotInteractor,
          unmarkNiceShotInteractor,
          shareShotInteractor,
          followInteractor,
          unfollowInteractor,
          getLastShotsInteractor,
          uploadUserPhotoInteractor,
          removeUserPhotoInteractor,
          errorMessageFactory,
          userModelMapper,
          shotModelMapper);
        profilePresenter.setView(profileView);
    }

    @Test public void shouldGetUserByIdIfItHasBeenInitializedWithUserId() throws Exception {
        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(getUserByIdInteractor).loadUserById(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldGetUserByUsernameIfItHasBeenInitializedWithUsername() throws Exception {
        profilePresenter.initializeWithUsername(profileView, USERNAME);

        verify(getUserByUsernameInteractor).searchUserByUsername(anyString(), anyCallback(), anyErrorCallback());
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

    @Test public void shouldLoadLastShotsWhenInitializedFromId() throws Exception {
        setupUserById();

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(getLastShotsInteractor).loadLastShots(eq(ID_USER), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldLoadLastShotsWhenInitializedFromUsername() throws Exception {
        setupUserByUsername();

        profilePresenter.initializeWithUsername(profileView, USERNAME);

        verify(getLastShotsInteractor).loadLastShots(eq(ID_USER), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldShowListingButtonWithCountWhenPresenterInitializedWithIdUserAndUserHasStreams() {
        setupUserById();

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showListingButtonWithCount(anyInt());
    }

    @Test public void shouldShowListingButtonWithCountWhenPresenterInitializedWithUsernameAndUserHasStreams() {
        setupUserByUsername();

        profilePresenter.initializeWithUsername(profileView, USERNAME);

        verify(profileView).showListingButtonWithCount(anyInt());
    }

    @Test public void shouldNotShowOpenStreamWhenInOtherUsersProfileWithoutCount() {
        User userWithoutListing = userWithCounts(0, 0);
        setupUserIdInteractorCallbacks(userWithoutListing);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView, never()).showOpenStream();
    }

    @Test public void shouldShowOpenStreamWhenInCurrentUsersProfileWithoutCount() {
        User userWithoutListing = userWithCounts(0, 0);
        userWithoutListing.setMe(true);
        setupUserIdInteractorCallbacks(userWithoutListing);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showOpenStream();
    }

    @Test public void shouldShowAllShotsIfUserHaveFourLatsShots() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(4));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showAllShotsButton();
    }

    @Test public void shouldHideAllShotsIfUserHaveThreeLatestShots() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(3));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).hideAllShotsButton();
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

    @Test public void shouldShowLogoutInProgressWhenLogoutSelected() {
        profilePresenter.logoutSelected();

        verify(profileView).showLogoutInProgress();
    }

    @Test public void shouldCallbackLogoutInteractorWhenLogoutSelected() {
        profilePresenter.logoutSelected();

        verify(logoutInteractor).attempLogout(anyCompletedCallback(), anyErrorCallback());
    }

    private Interactor.CompletedCallback anyCompletedCallback() {
        return any(Interactor.CompletedCallback.class);
    }

    @Test public void shouldNavigateToWelcomeScreenWhenLogoutSelectedAndCompletedCallback() {
        setupLogoutInteractorCompletedCallback();

        profilePresenter.logoutSelected();

        verify(profileView).navigateToWelcomeScreen();
    }

    @Test public void shouldHideLogoutInProgressWhenLogoutSelectedAndErrorCallback() {
        setupLogoutInteractorErrorCallback();

        profilePresenter.logoutSelected();

        verify(profileView).hideLogoutInProgress();
    }

    @Test public void shouldShowErrorWhenLogoutSelectedAndErrorCallback() {
        setupLogoutInteractorErrorCallback();

        profilePresenter.logoutSelected();

        verify(profileView).showError(anyString());
    }

    @Test public void shouldShowLogoutButtonWhenUserIsCurrentUser() {
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

    @Test public void shouldNavigateToStreamDetailWhenNewStreamCreatedIfSelectStreamInteractorCallbacksStreamId()
      throws Exception {
        profilePresenter.streamCreated(SELECTED_STREAM_ID);

        verify(profileView).navigateToCreatedStreamDetail(SELECTED_STREAM_ID);
    }

    @Test public void shouldShowLatestListIfThereAreShots() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(3));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showLatestShots();
    }

    @Test public void shouldHideLatestListIfThereAreNoShots() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(0));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).hideLatestShots();
    }

    @Test public void shouldShowLatestShotsEmptyIfThereAreNoShots() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(0));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).showLatestShotsEmpty();
    }

    @Test public void shouldHideShowLatestShotsEmptyIfThereAreShots() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(1));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).hideLatestShotsEmpty();
    }

    @Test public void shouldShowThreeLatestShotsWhenReceivedFour() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(4));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).renderLastShots(shotModelListCaptor.capture());
        assertThat(shotModelListCaptor.getValue()).hasSize(3);
    }

    @Test public void shouldShowThreeLatestShotsWhenReceivedThree() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(3));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).renderLastShots(shotModelListCaptor.capture());
        assertThat(shotModelListCaptor.getValue()).hasSize(3);
    }

    @Test public void shouldShowTwoLatestShotsWhenReceivedTwo() throws Exception {
        setupUserById();
        setupLatestShotCallbacks(shotList(2));

        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        verify(profileView).renderLastShots(shotModelListCaptor.capture());
        assertThat(shotModelListCaptor.getValue()).hasSize(2);
    }

    @Test public void shouldOpenWebsiteWithHttpPrefixWhenWebsiteHasNoHttpPrefix() throws Exception {
        User user = user();
        user.setWebsite(WEBSITE_NO_PREFIX);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        profilePresenter.websiteClicked();

        verify(profileView).goToWebsite(HTTP_PREFIX + WEBSITE_NO_PREFIX);
    }

    @Test public void shouldOpenWebsiteWithoutHttpPrefixWhenWebsiteHasHttpPrefix() throws Exception {
        User user = user();
        user.setWebsite(WEBSITE_HTTP_PREFIX);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        profilePresenter.websiteClicked();

        verify(profileView).goToWebsite(WEBSITE_HTTP_PREFIX);
    }

    @Test public void shouldOpenWebsiteWithoutHttpPrefixWhenWebsiteHasHttpsPrefix() throws Exception {
        User user = user();
        user.setWebsite(WEBSITE_HTTPS_PREFIX);
        setupUserIdInteractorCallbacks(user);

        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        profilePresenter.websiteClicked();

        verify(profileView).goToWebsite(WEBSITE_HTTPS_PREFIX);
    }

    @Test public void shouldGetUserByIdWhenResumed() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getUserByIdInteractor);

        profilePresenter.pause();
        profilePresenter.resume();

        verify(getUserByIdInteractor).loadUserById(eq(ID_USER), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldGetUserByIdWhenResumedIfInitializedByUsername() throws Exception {
        setupUserByUsername();
        profilePresenter.initializeWithUsername(profileView, USERNAME);

        profilePresenter.pause();
        profilePresenter.resume();

        verify(getUserByIdInteractor).loadUserById(eq(ID_USER), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldNotLoadProfileWhenIdInteractorNotReturned() throws Exception {
        doNothing().when(getUserByIdInteractor).loadUserById(anyString(), anyCallback(), anyErrorCallback());
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getUserByIdInteractor);

        profilePresenter.resume();

        verify(getUserByIdInteractor, never()).loadUserById(eq(ID_USER), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldNotLoadProfileWhenUsernameInteractorNotReturned() throws Exception {
        doNothing().when(getUserByUsernameInteractor)
          .searchUserByUsername(anyString(), anyCallback(), anyErrorCallback());
        profilePresenter.initializeWithUsername(profileView, USERNAME);
        reset(getUserByUsernameInteractor);

        profilePresenter.resume();

        verify(getUserByIdInteractor, never()).loadUserById(eq(ID_USER), anyCallback(), anyErrorCallback());
        verify(getUserByUsernameInteractor, never()).searchUserByUsername(anyString(),
          anyCallback(),
          anyErrorCallback());
    }

    @Test public void shouldLoadLatestShotsWhenResumed() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getLastShotsInteractor);

        profilePresenter.pause();
        profilePresenter.resume();

        verify(getLastShotsInteractor).loadLastShots(eq(ID_USER), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldNotLoadLatestShotsWhenInteractorNotReturned() throws Exception {
        doNothing().when(getUserByIdInteractor).loadUserById(anyString(), anyCallback(), anyErrorCallback());
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getLastShotsInteractor);

        profilePresenter.resume();

        verify(getLastShotsInteractor, never()).loadLastShots(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldShowLoadingWhenUploadingPhoto() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        profilePresenter.uploadPhoto(new File(PHOTO_PATH));

        verify(profileView).showLoadingPhoto();
    }

    @Test public void shouldCallUploadPhotoInteractorWhenUploadingPhoto() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);

        profilePresenter.uploadPhoto(new File(PHOTO_PATH));

        verify(uploadUserPhotoInteractor).uploadUserPhoto(any(File.class), anyCompletedCallback(), anyErrorCallback());
    }

    @Test public void shouldLoadProfileAgainWhenPhotoUploaded() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getUserByIdInteractor);

        setupUploadPhotoCompletedCallback();

        profilePresenter.uploadPhoto(new File(PHOTO_PATH));

        verify(getUserByIdInteractor).loadUserById(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldHideLoadingWhenUploadinPhotoCallbacksCompleted() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getUserByIdInteractor);

        setupUploadPhotoCompletedCallback();

        profilePresenter.uploadPhoto(new File(PHOTO_PATH));

        verify(profileView).hideLoadingPhoto();
    }

    @Test public void shouldHideLoadingWhenUploadinPhotoCallbacksError() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getUserByIdInteractor);

        setupUploadPhotoErrorCallback();

        profilePresenter.uploadPhoto(new File(PHOTO_PATH));

        verify(profileView).hideLoadingPhoto();
    }

    @Test public void shouldLoadProfileAgainWhenPhotoRemoved() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getUserByIdInteractor);

        setupRemovePhotoCompletedCallback();

        profilePresenter.removePhotoConfirmed();

        verify(getUserByIdInteractor).loadUserById(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test public void shouldShowErrorIfRemovePhotoCallbacksError() throws Exception {
        setupUserById();
        profilePresenter.initializeWithIdUser(profileView, ID_USER);
        reset(getUserByIdInteractor);

        setupRemovePhotoErrorCallback();

        profilePresenter.removePhotoConfirmed();

        verify(profileView).showError(anyString());
    }

    private void setupLogoutInteractorCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback =
                  (Interactor.CompletedCallback) invocation.getArguments()[0];
                completedCallback.onCompleted();
                return null;
            }
        }).when(logoutInteractor).attempLogout(anyCompletedCallback(), anyErrorCallback());
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    private void setupLogoutInteractorErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[1];
                errorCallback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(logoutInteractor).attempLogout(anyCompletedCallback(), anyErrorCallback());
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
        }).when(getUserByUsernameInteractor).searchUserByUsername(anyString(), anyCallback(), anyErrorCallback());
    }

    private Interactor.Callback anyCallback() {
        return any(Interactor.Callback.class);
    }

    private void setupUserById() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(user());
                return null;
            }
        }).when(getUserByIdInteractor).loadUserById(anyString(), anyCallback(), anyErrorCallback());
    }

    private List<Shot> shotList(int numberOfShots) {
        List<Shot> shots = new ArrayList<>();
        for (int i = 0; i < numberOfShots; i++) {
            Shot shot = new Shot();
            shot.setIdShot("idShot" + i);
            shot.setUserInfo(new Shot.ShotUserInfo());
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
        }).when(getUserByIdInteractor).loadUserById(anyString(), anyCallback(), anyErrorCallback());
    }

    private User userWithCounts(int createdCount, int favoritedCount) {
        User user = user();
        user.setCreatedStreamsCount((long) createdCount);
        user.setFavoritedStreamsCount((long) favoritedCount);
        return user;
    }

    private void setupLatestShotCallbacks(final List<Shot> result) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(result);
                return null;
            }
        }).when(getLastShotsInteractor).loadLastShots(anyString(), anyCallback(), anyErrorCallback());
    }

    private void setupUploadPhotoErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                callback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(uploadUserPhotoInteractor).uploadUserPhoto(any(File.class), anyCompletedCallback(), anyErrorCallback());
    }

    private void setupUploadPhotoCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback = (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(uploadUserPhotoInteractor).uploadUserPhoto(any(File.class), anyCompletedCallback(), anyErrorCallback());
    }

    private void setupRemovePhotoCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback = (Interactor.CompletedCallback) invocation.getArguments()[0];
                callback.onCompleted();
                return null;
            }
        }).when(removeUserPhotoInteractor).removeUserPhoto(anyCompletedCallback(), anyErrorCallback());
    }

    private void setupRemovePhotoErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback = (Interactor.ErrorCallback) invocation.getArguments()[1];
                callback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(removeUserPhotoInteractor).removeUserPhoto(anyCompletedCallback(), anyErrorCallback());
    }
}
