package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.repository.DatabaseUtils;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
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
    @Mock com.shootr.mobile.domain.service.user.CreateAccountGateway createAccountGateway;
    @Mock com.shootr.mobile.domain.service.user.LoginGateway loginGateway;
    @Mock com.shootr.mobile.domain.service.user.ResetPasswordGateway resetPasswordGateway;
    @Mock ConfirmEmailGateway confirmEmailGateway;
    @Mock StreamRepository remoteStreamRepository;
    @Mock com.shootr.mobile.domain.service.user.ChangePasswordGateway changePasswordGateway;
    @Mock UserRepository remoteUserRepository;
    @Mock ResetPasswordEmailGateway resetPasswordEmailGateway;
    @Mock DatabaseUtils databaseUtils;

    private com.shootr.mobile.domain.service.user.ShootrUserService shootrUserService;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shootrUserService = new com.shootr.mobile.domain.service.user.ShootrUserService(localUserRepository,
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