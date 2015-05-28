package com.shootr.android.domain.service.user;

import com.shootr.android.domain.ForgotPasswordResult;
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
    private final ResetPasswordGateway resetPasswordGateway;
    private final EventRepository remoteEventRepository;
    private final UserRepository remoteUserRepository;

    @Inject public ShootrUserService(@Local UserRepository localUserRepository, SessionRepository sessionRepository,
      CheckinGateway checkinGateway, CreateAccountGateway createAccountGateway, LoginGateway loginGateway,
      ResetPasswordGateway resetPasswordGateway, @Remote EventRepository remoteEventRepository, @Remote UserRepository remoteUserRepository) {
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
        this.checkinGateway = checkinGateway;
        this.createAccountGateway = createAccountGateway;
        this.loginGateway = loginGateway;
        this.resetPasswordGateway = resetPasswordGateway;
        this.remoteEventRepository = remoteEventRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void checkInEvent(String idEvent) {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        if (isCheckedInEvent(currentUser, idEvent)) {
            throw new InvalidCheckinException(String.format(
              "Can't perform checkin in event with id %s because user is already checked-in in it",
              idEvent));
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

    private boolean isCurrentUserCheckedInEvent(User currentUser, String idEvent) {
        if(currentUser.getIdCheckedEvent() != null){
            return currentUser.getIdCheckedEvent().equals(idEvent);
        }
        return false;
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

    public ForgotPasswordResult performResetPassword(String usernameOrEmail) {
        try {
            return resetPasswordGateway.performPasswordReset(usernameOrEmail);
        } catch (Exception e) {
            throw new AccountCreationException(e);
        }
    }
}
