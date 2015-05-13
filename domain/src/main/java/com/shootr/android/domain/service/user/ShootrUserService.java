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

    @Deprecated
    public void checkInCurrentEvent() {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        String watchingEventId = currentUser.getIdWatchingEvent();

        if (isCheckedInEvent(currentUser, watchingEventId)) {
            throw new InvalidCheckinException("Can't perform checkin if already checked-in");
        }

        if (watchingEventId == null) {
            throw new InvalidCheckinException("Can't perform checkin without visible event");
        }

        try {
            String idUser = currentUser.getIdUser();
            checkinGateway.performCheckin(idUser, watchingEventId);
        } catch (IOException e) {
            throw new InvalidCheckinException(e);
        }

        currentUser.setIdCheckedEvent(watchingEventId);
        localUserRepository.putUser(currentUser);
    }

    public void checkInEvent(String idEvent) {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        if (isCheckedInEvent(currentUser, idEvent)) {
            throw new InvalidCheckinException(
              "Can't perform checkin in event with id %s because user is already checked-in in it");
        }

        try {
            checkinGateway.performCheckin(currentUser.getIdUser(), idEvent);
        } catch (IOException e) {
            throw new InvalidCheckinException(e);
        }

        currentUser.setIdCheckedEvent(idEvent);
        localUserRepository.putUser(currentUser);
        sessionRepository.setCurrentUser(currentUser);
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
            String visibleEventId = loginResult.getUser().getIdWatchingEvent();
            if (visibleEventId != null) {
                remoteEventRepository.getEventById(visibleEventId);
            }
            remoteUserRepository.getPeople();
        } catch (IOException e) {
            throw new LoginException(e);
        }
    }

    public void checkOutCurrentEvent() {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        String idCheckedEvent = currentUser.getIdCheckedEvent();
        if (idCheckedEvent == null) {
            throw new InvalidCheckinException("Can't perform checkin without visible event");
        }
        String idUser = currentUser.getIdUser();
        try {
            checkinGateway.performCheckout(idUser, idCheckedEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentUser.setIdCheckedEvent(null);
        localUserRepository.putUser(currentUser);
    }

    private void storeSession(LoginResult loginResult) {
        String idUser = loginResult.getUser().getIdUser();
        String sessionToken = loginResult.getSessionToken();
        User user = loginResult.getUser();
        sessionRepository.createSession(idUser, sessionToken, user);
        localUserRepository.putUser(loginResult.getUser());
    }

    private boolean isCheckedInEvent(User user, String idEvent) {
        return user.getIdCheckedEvent() != null && user.getIdCheckedEvent().equals(idEvent);
    }
}
