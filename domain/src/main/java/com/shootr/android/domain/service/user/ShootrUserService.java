package com.shootr.android.domain.service.user;

import com.shootr.android.domain.ForgotPasswordResult;
import com.shootr.android.domain.LoginResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.EmailAlreadyExistsException;
import com.shootr.android.domain.exception.InvalidCheckinException;
import com.shootr.android.domain.exception.InvalidForgotPasswordException;
import com.shootr.android.domain.exception.InvalidLoginException;
import com.shootr.android.domain.exception.InvalidPasswordException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.UsernameAlreadyExistsException;
import com.shootr.android.domain.repository.DatabaseUtils;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.io.IOException;
import javax.inject.Inject;

public class ShootrUserService {

    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private final CheckinGateway checkinGateway;
    private final CreateAccountGateway createAccountGateway;
    private final LoginGateway loginGateway;
    private final ResetPasswordGateway resetPasswordGateway;
    private final ChangePasswordGateway changePasswordGateway;
    private final EventRepository remoteEventRepository;
    private final UserRepository remoteUserRepository;
    private final ResetPasswordEmailGateway resetPasswordEmailGateway;
    private final DatabaseUtils databaseUtils;

    @Inject public ShootrUserService(@Local UserRepository localUserRepository, SessionRepository sessionRepository,
      CheckinGateway checkinGateway, CreateAccountGateway createAccountGateway, LoginGateway loginGateway,
      ResetPasswordGateway resetPasswordGateway, ChangePasswordGateway changePasswordGateway, @Remote EventRepository remoteEventRepository,
      @Remote UserRepository remoteUserRepository, ResetPasswordEmailGateway resetPasswordEmailGateway, DatabaseUtils databaseUtils) {
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
        this.checkinGateway = checkinGateway;
        this.createAccountGateway = createAccountGateway;
        this.loginGateway = loginGateway;
        this.resetPasswordGateway = resetPasswordGateway;
        this.changePasswordGateway = changePasswordGateway;
        this.remoteEventRepository = remoteEventRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.resetPasswordEmailGateway = resetPasswordEmailGateway;
        this.databaseUtils = databaseUtils;
    }

    public void checkInEvent(String idEvent) {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        try {
            checkinGateway.performCheckin(currentUser.getIdUser(), idEvent);
        } catch (IOException e) {
            throw new InvalidCheckinException(e);
        }
    }

    public void createAccount(String username, String email, String password)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        LoginResult loginResult = createAccountGateway.performCreateAccount(username, email, password);
        retrievePostLoginInformation(loginResult);
    }

    public void performLogin(String usernameOrEmail, String password) throws InvalidLoginException {
        LoginResult loginResult = loginGateway.performLogin(usernameOrEmail, password);
        retrievePostLoginInformation(loginResult);
    }

    public void performFacebookLogin(String facebookToken) throws InvalidLoginException{
        LoginResult loginResult = loginGateway.performFacebookLogin(facebookToken);
        retrievePostLoginInformation(loginResult);
    }

    private void retrievePostLoginInformation(LoginResult loginResult) {
        storeSession(loginResult);
        String visibleEventId = loginResult.getUser().getIdWatchingEvent();
        if (visibleEventId != null) {
            remoteEventRepository.getEventById(visibleEventId);
        }
        remoteUserRepository.getPeople();
    }

    private void storeSession(LoginResult loginResult) {
        String idUser = loginResult.getUser().getIdUser();
        String sessionToken = loginResult.getSessionToken();
        User user = loginResult.getUser();
        sessionRepository.createSession(idUser, sessionToken, user);
        localUserRepository.putUser(loginResult.getUser());
    }

    public ForgotPasswordResult performResetPassword(String usernameOrEmail)
      throws InvalidForgotPasswordException, IOException {
        return resetPasswordGateway.performPasswordReset(usernameOrEmail);
    }

    public void sendPasswordResetEmail(String idUser) throws IOException {
        resetPasswordEmailGateway.sendPasswordResetEmail(idUser);
    }

    public void performLogout() {
        try {
            User currentUser = sessionRepository.getCurrentUser();
            loginGateway.performLogout(currentUser.getIdUser());
            removeSession();
            databaseUtils.clearDataOnLogout();
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    private void removeSession() {
        sessionRepository.destroySession();
    }

    public void changePassword(String currentPassword, String newPassword) throws InvalidPasswordException {
        changePasswordGateway.changePassword(currentPassword, newPassword);
    }
}
