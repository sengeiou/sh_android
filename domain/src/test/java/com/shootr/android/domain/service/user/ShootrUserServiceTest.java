package com.shootr.android.domain.service.user;

import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidCheckinException;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrUserServiceTest {

    private static final String CURRENT_USER_ID = "1L";
    private static final String VISIBLE_EVENT_ID = "2L";
    public static final String NO_VISIBLE_EVENT = null;
    private static final String USERNAME_OR_EMAIL_STUB = "username_or_email";
    private static final String PASSWORD_STUB = "password";

    private ShootrUserService shootrUserService;
    @Mock UserRepository localUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock CheckinGateway checkinGateway;
    @Mock CreateAccountGateway createAccountGateway;
    @Mock LoginGateway loginGateway;
    @Mock EventRepository remoteEventRepository;
    @Mock UserRepository remoteUserRepository;
    public static final String SESSION_TOKEN_STUB = "sessionToken";

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrUserService = new ShootrUserService(localUserRepository, sessionRepository, checkinGateway,
          createAccountGateway, loginGateway, remoteEventRepository, remoteUserRepository);
    }

    @Test(expected = InvalidCheckinException.class) public void shouldFailIfCurrentUserIsCheckedIn() throws Exception {
        setupCurrentUserCheckedIn();
        shootrUserService.checkInCurrentEvent(idEvent);
    }

    @Test(expected = InvalidCheckinException.class) public void shouldFailIfNoVisibleEvent() throws Exception {
        setupCurrentUserWithoutVisibleEvent();

        shootrUserService.checkInCurrentEvent(idEvent);
    }

    @Test public void shouldCallGatewayWithCurrentUserIdAndEvent() throws Exception {
        setupCurrentUserNotCheckedIn();

        shootrUserService.checkInCurrentEvent(idEvent);

        verify(checkinGateway).performCheckin(CURRENT_USER_ID, VISIBLE_EVENT_ID);
    }

    @Test public void shouldStoreCurrentUserInLocalCheckedIn() throws Exception {
        setupCurrentUserNotCheckedIn();

        shootrUserService.checkInCurrentEvent(idEvent);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(localUserRepository).putUser(userCaptor.capture());
        User userStoredInLocal = userCaptor.getValue();
        assertThat(userStoredInLocal.isCheckedIn()).isTrue();
    }

    @Test public void shouldCreateSessionWhenLoginCorrect() throws IOException {
        when(loginGateway.performLogin(anyString(), anyString())).thenReturn(loginResultCorrect());

        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);

        verify(sessionRepository).createSession(CURRENT_USER_ID, SESSION_TOKEN_STUB, currentUser());
    }

    @Test public void shouldDownloadEventIfUserHasEventsWhenLoginCorrect() throws IOException {
        when(loginGateway.performLogin(anyString(),anyString())).thenReturn(loginResultWithEvent());

        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);

        verify(remoteEventRepository).getEventById(VISIBLE_EVENT_ID);
    }

    @Test public void shouldNotDownloadAnyEventIfUserHasNotEventsWhenLoginCorrect() throws IOException {
        when(loginGateway.performLogin(anyString(),anyString())).thenReturn(loginResultWithoutEvent());
        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);
        verify(remoteEventRepository, never()).getEventById(anyString());
    }

    @Test public void shouldDownloadPeopleIfUserHasEventsWhenLoginCorrect() throws IOException {
        when(loginGateway.performLogin(anyString(),anyString())).thenReturn(loginResultWithEvent());
        shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);
        verify(remoteUserRepository).getPeople();
    }

    @Test public void shouldDownlaodPeopleIfUserHasNotEventsWhenLoginCorrect() throws IOException {
        when(loginGateway.performLogin(anyString(),anyString())).thenReturn(loginResultWithoutEvent());
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
        user.setIdWatchingEvent(String.valueOf(VISIBLE_EVENT_ID));
        return new LoginResult(user, SESSION_TOKEN_STUB);
    }

    private LoginResult loginResultCorrect() {
        return new LoginResult(currentUser(), SESSION_TOKEN_STUB);
    }

    private void setupCurrentUserWithoutVisibleEvent() {
        User currentUser = currentUser();
        currentUser.setIdCheckedEvent(false);
        currentUser.setIdWatchingEvent(NO_VISIBLE_EVENT);
        when(localUserRepository.getUserById(anyString())).thenReturn(currentUser);
    }

    private void setupCurrentUserNotCheckedIn() {
        User currentUser = currentUser();
        currentUser.setIdCheckedEvent(false);
        when(localUserRepository.getUserById(anyString())).thenReturn(currentUser);
    }

    private void setupCurrentUserCheckedIn() {
        User currentUser = currentUser();
        currentUser.setIdCheckedEvent(true);
        when(localUserRepository.getUserById(anyString())).thenReturn(currentUser);
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(String.valueOf(CURRENT_USER_ID));
        user.setIdWatchingEvent(String.valueOf(VISIBLE_EVENT_ID));
        return user;
    }
}