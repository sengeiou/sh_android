package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetListingCountInteractor;
import com.shootr.android.domain.interactor.user.GetCurrentUserInteractor;
import com.shootr.android.domain.interactor.user.LogoutInteractor;
import com.shootr.android.ui.views.ProfileView;
import com.shootr.android.util.DatabaseVersionUtils;
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
    @Mock GetCurrentUserInteractor getCurrentUserInteractor;
    @Mock LogoutInteractor logoutInteractor;
    @Mock DatabaseVersionUtils databaseVersionUtils;
    @Mock ProfileView profileView;

    private ProfilePresenter profilePresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        profilePresenter = new ProfilePresenter(getListingCountInteractor, getCurrentUserInteractor, logoutInteractor,
          databaseVersionUtils);
        profilePresenter.setView(profileView);
    }

    @Test
    public void shouldLoadCurrentUserListingWhenPresenterInitialized() {
        profilePresenter.initialize(profileView, ID_USER);

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
    public void shouldHideLogoutInProgressWhenLogoutSelectedAndCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback = (Interactor.CompletedCallback) invocation.getArguments()[0];
                completedCallback.onCompleted();
                return null;
            }
        }).when(logoutInteractor)
          .attempLogout(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));

        profilePresenter.logoutSelected();

        verify(profileView).hideLogoutInProgress();
    }

    @Test
    public void shouldNavigateToWelcomeScreenWhenLogoutSelectedAndCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback = (Interactor.CompletedCallback) invocation.getArguments()[0];
                completedCallback.onCompleted();
                return null;
            }
        }).when(logoutInteractor)
          .attempLogout(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));

        profilePresenter.logoutSelected();

        verify(profileView).navigateToWelcomeScreen();
    }

    @Test
    public void shouldHideLogoutInProgressWhenLogoutSelectedAndErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[1];
                errorCallback.onError(new ShootrException() {
                    @Override public Throwable fillInStackTrace() {
                        return super.fillInStackTrace();
                    }
                });
                return null;
            }
        }).when(logoutInteractor)
          .attempLogout(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));

        profilePresenter.logoutSelected();

        verify(profileView).hideLogoutInProgress();
    }

    @Test
    public void shouldShowErrorWhenLogoutSelectedAndErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[1];
                errorCallback.onError(new ShootrException() {
                    @Override public Throwable fillInStackTrace() {
                        return super.fillInStackTrace();
                    }
                });
                return null;
            }
        }).when(logoutInteractor)
          .attempLogout(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));

        profilePresenter.logoutSelected();

        verify(profileView).showError(any(ShootrException.class));
    }
}
