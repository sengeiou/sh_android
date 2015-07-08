package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetListingCountInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.ui.views.ProfileView;
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

public class ProfilePresenterTest {

    public static final String ID_USER = "id_user";
    
    @Mock GetListingCountInteractor getListingCountInteractor;
    @Mock LogoutInteractor logoutInteractor;
    @Mock ProfileView profileView;

    private ProfilePresenter profilePresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        profilePresenter = new ProfilePresenter(getListingCountInteractor, logoutInteractor);
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
