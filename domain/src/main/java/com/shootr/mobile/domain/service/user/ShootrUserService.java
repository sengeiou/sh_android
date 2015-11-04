package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.ForgotPasswordResult;
import com.shootr.mobile.domain.LoginResult;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.InvalidEmailConfirmationException;
import com.shootr.mobile.domain.exception.InvalidLoginException;
import com.shootr.mobile.domain.exception.InvalidPasswordException;
import com.shootr.mobile.domain.exception.UnauthorizedRequestException;
import com.shootr.mobile.domain.repository.DatabaseUtils;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.io.IOException;
import javax.inject.Inject;

public class ShootrUserService {

    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private final CheckinGateway checkinGateway;
    private final com.shootr.mobile.domain.service.user.CreateAccountGateway createAccountGateway;
    private final com.shootr.mobile.domain.service.user.LoginGateway loginGateway;
    private final com.shootr.mobile.domain.service.user.ResetPasswordGateway resetPasswordGateway;
    private final ConfirmEmailGateway confirmEmailGateway;
    private final StreamRepository remoteStreamRepository;
    private final com.shootr.mobile.domain.service.user.ChangePasswordGateway changePasswordGateway;
    private final UserRepository remoteUserRepository;
    private final ResetPasswordEmailGateway resetPasswordEmailGateway;
    private final DatabaseUtils databaseUtils;

    @Inject public ShootrUserService(@Local UserRepository localUserRepository, SessionRepository sessionRepository,
      CheckinGateway checkinGateway, com.shootr.mobile.domain.service.user.CreateAccountGateway createAccountGateway, com.shootr.mobile.domain.service.user.LoginGateway loginGateway,
      com.shootr.mobile.domain.service.user.ResetPasswordGateway resetPasswordGateway, com.shootr.mobile.domain.service.user.ChangePasswordGateway changePasswordGateway, ConfirmEmailGateway confirmEmailGateway, @com.shootr.mobile.domain.repository.Remote
    StreamRepository remoteStreamRepository,
      @com.shootr.mobile.domain.repository.Remote UserRepository remoteUserRepository, ResetPasswordEmailGateway resetPasswordEmailGateway, DatabaseUtils databaseUtils) {
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
        this.checkinGateway = checkinGateway;
        this.createAccountGateway = createAccountGateway;
        this.loginGateway = loginGateway;
        this.resetPasswordGateway = resetPasswordGateway;
        this.confirmEmailGateway = confirmEmailGateway;
        this.remoteStreamRepository = remoteStreamRepository;
        this.changePasswordGateway = changePasswordGateway;
        this.remoteUserRepository = remoteUserRepository;
        this.resetPasswordEmailGateway = resetPasswordEmailGateway;
        this.databaseUtils = databaseUtils;
    }

    public void checkInStream(String idEvent) {
        checkinGateway.performCheckin(idEvent);
    }

    public void createAccount(String username, String email, String password)
      throws EmailAlreadyExistsException, com.shootr.mobile.domain.exception.UsernameAlreadyExistsException {
        LoginResult loginResult = createAccountGateway.performCreateAccount(username, email, password);
        retrievePostLoginInformation(loginResult);
    }

    public void performLogin(String usernameOrEmail, String password) throws InvalidLoginException {
        LoginResult loginResult = loginGateway.performLogin(usernameOrEmail, password);
        retrievePostLoginInformation(loginResult);
    }

    public Boolean performFacebookLogin(String facebookToken) throws InvalidLoginException{
        LoginResult loginResult = loginGateway.performFacebookLogin(facebookToken);
        retrievePostLoginInformation(loginResult);
        return loginResult.isNewUser();
    }

    private void retrievePostLoginInformation(LoginResult loginResult) {
        storeSession(loginResult);
        String visibleEventId = loginResult.getUser().getIdWatchingStream();
        if (visibleEventId != null) {
            remoteStreamRepository.getStreamById(visibleEventId);
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
      throws com.shootr.mobile.domain.exception.InvalidForgotPasswordException, IOException {
        return resetPasswordGateway.performPasswordReset(usernameOrEmail);
    }

    public void sendPasswordResetEmail(String idUser) throws IOException {
        resetPasswordEmailGateway.sendPasswordResetEmail(idUser);
    }

    public void performLogout() {
        User currentUser = sessionRepository.getCurrentUser();
        loginGateway.performLogout(currentUser.getIdUser());
        removeSession();
        databaseUtils.clearDataOnLogout();
    }

    private void removeSession() {
        sessionRepository.destroySession();
    }

    public void requestEmailConfirmation() throws InvalidEmailConfirmationException {
        confirmEmailGateway.confirmEmail();
    }

    public void changeEmail(String email)
      throws EmailAlreadyExistsException, EmailAlreadyConfirmedException, UnauthorizedRequestException {
        String currentUserId = sessionRepository.getCurrentUserId();
        User user = localUserRepository.getUserById(currentUserId);
        user.setEmail(email);
        user.setEmailConfirmed(false);
        sessionRepository.setCurrentUser(user);
        localUserRepository.putUser(user);

        confirmEmailGateway.changeEmail(email);
    }

    public void changePassword(String currentPassword, String newPassword) throws InvalidPasswordException {
        changePasswordGateway.changePassword(currentPassword, newPassword);
    }

    public void removeSessionData() {
        removeSession();
        databaseUtils.clearDataOnLogout();
    }
}