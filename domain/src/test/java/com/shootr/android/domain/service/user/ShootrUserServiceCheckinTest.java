package com.shootr.android.domain.service.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidCheckinException;
import com.shootr.android.domain.repository.DatabaseUtils;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrUserServiceCheckinTest {

    private static final String CURRENT_USER_ID = "user_id";
    private static final String CHECKED_EVENT_ID = "checked_event_id";
    private static final String STUB_EVENT_ID = "event_id";

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

    @Test(expected = InvalidCheckinException.class) public void shouldFailIfCurrentUserIsCheckedInSameEvent() throws Exception {
        setupCurrentUserCheckedIn(CHECKED_EVENT_ID);
        shootrUserService.checkInEvent(CHECKED_EVENT_ID);
    }

    @Test public void shouldCallGatewayWithCurrentUserIdAndEvent() throws Exception {
        setupCurrentUserNotCheckedIn();

        shootrUserService.checkInEvent(STUB_EVENT_ID);

        verify(checkinGateway).performCheckin(CURRENT_USER_ID, STUB_EVENT_ID);
    }

    @Test public void shouldStoreCurrentUserWithCheckedInEventInLocalRepository() throws Exception {
        setupCurrentUserNotCheckedIn();

        shootrUserService.checkInEvent(STUB_EVENT_ID);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(localUserRepository).putUser(userCaptor.capture());
        User userStoredInLocal = userCaptor.getValue();
        assertThat(userStoredInLocal.getIdCheckedEvent()).isEqualTo(STUB_EVENT_ID);
    }

    @Test public void shouldStoreCurrentUserWithCheckedInEventInSessionRepository() throws Exception {
        setupCurrentUserNotCheckedIn();

        shootrUserService.checkInEvent(STUB_EVENT_ID);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(sessionRepository).setCurrentUser(userCaptor.capture());
        User userStoredInLocal = userCaptor.getValue();
        assertThat(userStoredInLocal.getIdCheckedEvent()).isEqualTo(STUB_EVENT_ID);
    }

    private void setupCurrentUserNotCheckedIn() {
        User currentUser = currentUser();
        when(localUserRepository.getUserById(anyString())).thenReturn(currentUser);
    }

    private void setupCurrentUserCheckedIn(String idEvent) {
        User currentUser = currentUser();
        currentUser.setIdCheckedEvent(idEvent);
        when(localUserRepository.getUserById(anyString())).thenReturn(currentUser);
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        return user;
    }
}