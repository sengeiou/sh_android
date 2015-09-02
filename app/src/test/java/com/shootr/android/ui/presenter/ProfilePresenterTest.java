package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.ShareShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.domain.interactor.stream.GetListingCountInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ProfilePresenterTest {

    public static final String ID_USER = "id_user";
    public static final String SELECTED_STREAM_ID = "selected_stream_id";

    @Mock GetListingCountInteractor getListingCountInteractor;
    @Mock LogoutInteractor logoutInteractor;
    @Mock ProfileView profileView;
    @Mock MarkNiceShotInteractor markNiceShotInteractor;
    @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    @Mock ShareShotInteractor shareShotInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    private ProfilePresenter profilePresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        profilePresenter = new ProfilePresenter(getListingCountInteractor, logoutInteractor,
          markNiceShotInteractor,
          unmarkNiceShotInteractor, shareShotInteractor, errorMessageFactory);
        profilePresenter.setView(profileView);
        profilePresenter.setCurrentUser(true);
    }

    @Test
    public void shouldLoadCurrentUserListingWhenPresenterInitialized() {
        profilePresenter.initialize(profileView, ID_USER, false);

        verify(getListingCountInteractor).loadListingCount(anyString(), any(Interactor.Callback.class));
    }

    @Test
    public void shouldNavigateToListingWhenListingClicked() {
        profilePresenter.clickListing();

        verify(profileView).navigateToListing(anyString());
    }

    @Test
    public void shouldShowCurrentUserListingWhenPresenterInitializedAndUserHasStreams() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(1);
                return null;
            }
        }).when(getListingCountInteractor)
          .loadListingCount(anyString(), any(Interactor.Callback.class));

        profilePresenter.initialize(profileView, ID_USER, false);

        verify(profileView).showListingCount(anyInt());
    }

    @Test
    public void shouldShowOpenStreamWhenPresenterInitializedAndUserHasNoStreams() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(0);
                return null;
            }
        }).when(getListingCountInteractor)
          .loadListingCount(anyString(), any(Interactor.Callback.class));

        profilePresenter.initialize(profileView, ID_USER, true);

        verify(profileView).showOpenStream();
    }

    @Test
    public void shouldNotShowOpenStreamWhenInOtherUsersProfile() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback callback = (Interactor.Callback) invocation.getArguments()[1];
                callback.onLoaded(0);
                return null;
            }
        }).when(getListingCountInteractor)
          .loadListingCount(anyString(), any(Interactor.Callback.class));

        profilePresenter.initialize(profileView, ID_USER, false);

        verify(profileView, never()).showOpenStream();
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
        profilePresenter.initialize(profileView, ID_USER, true);

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

}
