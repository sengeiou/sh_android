package com.shootr.android.domain.service.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidCheckinException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrUserServiceTest {

    private static final Long CURRENT_USER_ID = 1L;
    private static final Long VISIBLE_EVENT_ID = 2L;
    public static final Long NO_VISIBLE_EVENT = null;

    private ShootrUserService shootrUserService;
    @Mock UserRepository localUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock CheckinGateway checkinGateway;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrUserService = new ShootrUserService(localUserRepository, sessionRepository, checkinGateway);
    }

    @Test(expected = InvalidCheckinException.class) public void shouldFailIfCurrentUserIsCheckedIn() throws Exception {
        setupCurrentUserCheckedIn();
        shootrUserService.checkInCurrentEvent();
    }

    @Test(expected = InvalidCheckinException.class) public void shouldFailIfNoVisibleEvent() throws Exception {
        setupCurrentUserWithoutVisibleEvent();

        shootrUserService.checkInCurrentEvent();
    }

    @Test public void shouldCallGatewayWithCurrentUserIdAndEvent() throws Exception {
        setupCurrentUserNotCheckedIn();

        shootrUserService.checkInCurrentEvent();

        verify(checkinGateway).performCheckin(CURRENT_USER_ID, VISIBLE_EVENT_ID);
    }

    private void setupCurrentUserWithoutVisibleEvent() {
        User currentUser = currentUser();
        currentUser.setCheckedIn(false);
        currentUser.setVisibleEventId(NO_VISIBLE_EVENT);
        when(localUserRepository.getUserById(anyLong())).thenReturn(currentUser);
    }

    private void setupCurrentUserNotCheckedIn() {
        User currentUser = currentUser();
        currentUser.setCheckedIn(false);
        when(localUserRepository.getUserById(anyLong())).thenReturn(currentUser);
    }

    private void setupCurrentUserCheckedIn() {
        User currentUser = currentUser();
        currentUser.setCheckedIn(true);
        when(localUserRepository.getUserById(anyLong())).thenReturn(currentUser);
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        user.setVisibleEventId(VISIBLE_EVENT_ID);
        return user;
    }
}