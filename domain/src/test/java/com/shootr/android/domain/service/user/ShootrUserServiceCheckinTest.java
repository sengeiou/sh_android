package com.shootr.android.domain.service.user;

import com.shootr.android.domain.repository.DatabaseUtils;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class ShootrUserServiceCheckinTest {

    private static final String STUB_STREAM_ID = "stream_id";

    @Mock UserRepository localUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock CheckinGateway checkinGateway;
    @Mock CreateAccountGateway createAccountGateway;
    @Mock LoginGateway loginGateway;
    @Mock ResetPasswordGateway resetPasswordGateway;
    @Mock ConfirmEmailGateway confirmEmailGateway;
    @Mock StreamRepository remoteStreamRepository;
    @Mock ChangePasswordGateway changePasswordGateway;
    @Mock UserRepository remoteUserRepository;
    @Mock ResetPasswordEmailGateway resetPasswordEmailGateway;
    @Mock DatabaseUtils databaseUtils;

    private ShootrUserService shootrUserService;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrUserService = new ShootrUserService(localUserRepository,
          sessionRepository,
          checkinGateway,
          createAccountGateway,
          loginGateway,
          resetPasswordGateway,
          changePasswordGateway,
          confirmEmailGateway,
          remoteStreamRepository,
          remoteUserRepository,
          resetPasswordEmailGateway,
          databaseUtils);
    }

    @Test public void shouldCallGatewayWithCurrentUserIdAndStream() throws Exception {
        shootrUserService.checkInStream(STUB_STREAM_ID);

        verify(checkinGateway).performCheckin(STUB_STREAM_ID);
    }
}