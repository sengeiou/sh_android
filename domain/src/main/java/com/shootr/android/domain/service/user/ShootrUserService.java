package com.shootr.android.domain.service.user;

import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidCheckinException;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.utils.SecurityUtils;
import java.io.IOException;
import javax.inject.Inject;

public class ShootrUserService {

    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private final CheckinGateway checkinGateway;
    private final CreateAccountGateway createAccountGateway;
    private final LoginGateway loginGateway;
    private final EventRepository remoteEventRepository;
    private final UserRepository remoteUserRepository;

    @Inject public ShootrUserService(@Local UserRepository localUserRepository, SessionRepository sessionRepository,
      CheckinGateway checkinGateway, CreateAccountGateway createAccountGateway, LoginGateway loginGateway,
      @Remote EventRepository remoteEventRepository, @Remote UserRepository remoteUserRepository) {
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
        this.checkinGateway = checkinGateway;
        this.createAccountGateway = createAccountGateway;
        this.loginGateway = loginGateway;
        this.remoteEventRepository = remoteEventRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void checkInCurrentEvent() {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        if (currentUser.isCheckedIn()) {
            throw new InvalidCheckinException("Can't perform checkin if already checked-in");
        }
        Long visibleEventId = currentUser.getVisibleEventId();
        if (visibleEventId == null) {
            throw new InvalidCheckinException("Can't perform checkin without visible event");
        }

        try {
            checkinGateway.performCheckin(currentUser.getIdUser(), visibleEventId);
        } catch (IOException e) {
            throw new InvalidCheckinException(e);
        }

        currentUser.setCheckedIn(true);
        localUserRepository.putUser(currentUser);
    }

    public void createAccount(String username, String email, String password) {
        String hashedPassword = SecurityUtils.encodePassword(password);
        try {
            createAccountGateway.performCreateAccount(username, email, hashedPassword);
        } catch (Exception e) {
            throw new AccountCreationException(e);
        }
    }

    public void performLogin(String usernameOrEmail, String password) {
        try {
            LoginResult loginResult = loginGateway.performLogin(usernameOrEmail, password);
            storeSession(loginResult);
            Long visibleEventId = loginResult.getUser().getVisibleEventId();
            if(visibleEventId !=null){
                remoteEventRepository.getEventById(visibleEventId);
            }
            remoteUserRepository.getPeople();
        } catch (IOException e) {
            throw new LoginException(e);
        }
    }

    private void storeSession(LoginResult loginResult) {
        sessionRepository.createSession(loginResult.getUser().getIdUser(), loginResult.getSessionToken(), loginResult.getUser());
        localUserRepository.putUser(loginResult.getUser());
    }

}
