package com.shootr.android.domain.service.user;

import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.DatabaseUtils;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrUserServiceLoginTest {

    private static final String CURRENT_USER_ID = "user_id";
    private static final String WATCHING_EVENT_ID = "watching_id";
    private static final String USERNAME_OR_EMAIL_STUB = "username_or_email";
    private static final String SESSION_TOKEN_STUB = "sessionToken";

    private static final String PASSWORD_STUB = "password";
    @Mock UserRepository localUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock CheckinGateway checkinGateway;
    @Mock CreateAccountGateway createAccountGateway;
    @Mock LoginGateway loginGateway;
    @Mock ResetPasswordGateway resetPasswordGateway;
    @Mock ConfirmEmailGateway confirmEmailGateway;
    @Mock EventRepository remoteEventRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock ResetPasswordEmailGateway resetPasswordEmailGateway;
    @Mock DatabaseUtils databaseUtils;

    private ShootrUserService shootrUserService;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrUserService = new ShootrUserService(localUserRepository, sessionRepository, checkinGateway,
          createAccountGateway, loginGateway, resetPasswordGateway,
          confirmEmailGateway,
          remoteEventRepository, remoteUserRepository,
          resetPasswordEmailGateway, databaseUtils);
    }

    @Test public void shouldCreateSessionWhenLoginCorrect() throws Exception {
        when(loginGateway.performLogin(anyString(), anyString())).thenReturn(loginResultCorrect());

        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);

        verify(sessionRepository).createSession(CURRENT_USER_ID, SESSION_TOKEN_STUB, currentUser());
    }

    @Test public void shouldDownloadEventIfUserHasEventsWhenLoginCorrect() throws Exception {
        when(loginGateway.performLogin(anyString(),anyString())).thenReturn(loginResultWithEvent());

        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);

        verify(remoteEventRepository).getEventById(WATCHING_EVENT_ID);
    }

    @Test public void shouldNotDownloadAnyEventIfUserHasNotEventsWhenLoginCorrect() throws Exception {
        when(loginGateway.performLogin(anyString(),anyString())).thenReturn(loginResultWithoutEvent());
        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);
        verify(remoteEventRepository, never()).getEventById(anyString());
    }

    @Test public void shouldDownloadPeopleIfUserHasEventsWhenLoginCorrect() throws Exception {
        when(loginGateway.performLogin(anyString(),anyString())).thenReturn(loginResultWithEvent());
        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);
        verify(remoteUserRepository).getPeople();
    }

    @Test public void shouldDownlaodPeopleIfUserHasNotEventsWhenLoginCorrect() throws Exception {
        when(loginGateway.performLogin(anyString(), anyString())).thenReturn(loginResultWithoutEvent());
        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);
        verify(remoteUserRepository).getPeople();
    }

    private LoginResult loginResultWithoutEvent() {
        User user = currentUserWithoutEvents();
        return new LoginResult(user,SESSION_TOKEN_STUB);
    }

    private User currentUserWithoutEvents() {
        User user = new User();
        user.setIdUser(String.valueOf(CURRENT_USER_ID));
        return user;
    }

    private LoginResult loginResultWithEvent() {
        User user = currentUser();
        user.setIdWatchingEvent(String.valueOf(WATCHING_EVENT_ID));
        return new LoginResult(user, SESSION_TOKEN_STUB);
    }

    private LoginResult loginResultCorrect() {
        return new LoginResult(currentUser(), SESSION_TOKEN_STUB);
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(String.valueOf(CURRENT_USER_ID));
        user.setIdWatchingEvent(WATCHING_EVENT_ID);
        return user;
    }
}