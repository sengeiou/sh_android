package com.shootr.android.domain.service.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.DatabaseUtils;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrUserServiceCheckinTest {

    private static final String CURRENT_USER_ID = "user_id";
    private static final String STUB_EVENT_ID = "event_id";

    @Mock UserRepository localUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock CheckinGateway checkinGateway;
    @Mock CreateAccountGateway createAccountGateway;
    @Mock LoginGateway loginGateway;
    @Mock ResetPasswordGateway resetPasswordGateway;
    @Mock StreamRepository remoteStreamRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock ResetPasswordEmailGateway resetPasswordEmailGateway;
    @Mock DatabaseUtils databaseUtils;

    private ShootrUserService shootrUserService;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrUserService = new ShootrUserService(localUserRepository, sessionRepository, checkinGateway,
          createAccountGateway, loginGateway, resetPasswordGateway, remoteStreamRepository, remoteUserRepository,
          resetPasswordEmailGateway, databaseUtils);
    }

    @Test public void shouldCallGatewayWithCurrentUserIdAndEvent() throws Exception {
        setupCurrentUser();

        shootrUserService.checkInEvent(STUB_EVENT_ID);

        verify(checkinGateway).performCheckin(CURRENT_USER_ID, STUB_EVENT_ID);
    }


    private void setupCurrentUser() {
        when(localUserRepository.getUserById(anyString())).thenReturn(currentUser());
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        return user;
    }
}