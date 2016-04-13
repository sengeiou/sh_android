package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.ForgotPasswordResult;
import com.shootr.mobile.domain.LoginResult;
import com.shootr.mobile.domain.Nicer;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.InvalidEmailConfirmationException;
import com.shootr.mobile.domain.exception.InvalidForgotPasswordException;
import com.shootr.mobile.domain.exception.InvalidLoginException;
import com.shootr.mobile.domain.exception.InvalidPasswordException;
import com.shootr.mobile.domain.exception.UnauthorizedRequestException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.repository.DatabaseUtils;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.NicerRepository;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShootrUserService {

    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private final CreateAccountGateway createAccountGateway;
    private final LoginGateway loginGateway;
    private final ResetPasswordGateway resetPasswordGateway;
    private final ConfirmEmailGateway confirmEmailGateway;
    private final StreamRepository remoteStreamRepository;
    private final ChangePasswordGateway changePasswordGateway;
    private final UserRepository remoteUserRepository;
    private final ResetPasswordEmailGateway resetPasswordEmailGateway;
    private final DatabaseUtils databaseUtils;
    private final NicerRepository nicerRepository;

    @Inject
    public ShootrUserService(@Local UserRepository localUserRepository, SessionRepository sessionRepository,
      CreateAccountGateway createAccountGateway, LoginGateway loginGateway,
      ResetPasswordGateway resetPasswordGateway, ChangePasswordGateway changePasswordGateway,
      ConfirmEmailGateway confirmEmailGateway, @Remote StreamRepository remoteStreamRepository,
      @Remote UserRepository remoteUserRepository, ResetPasswordEmailGateway resetPasswordEmailGateway,
      DatabaseUtils databaseUtils, NicerRepository nicerRepository) {
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
        this.createAccountGateway = createAccountGateway;
        this.loginGateway = loginGateway;
        this.resetPasswordGateway = resetPasswordGateway;
        this.confirmEmailGateway = confirmEmailGateway;
        this.remoteStreamRepository = remoteStreamRepository;
        this.changePasswordGateway = changePasswordGateway;
        this.remoteUserRepository = remoteUserRepository;
        this.resetPasswordEmailGateway = resetPasswordEmailGateway;
        this.databaseUtils = databaseUtils;
        this.nicerRepository = nicerRepository;
    }

    public void createAccount(String username, String email, String password, String locale)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        LoginResult loginResult = createAccountGateway.performCreateAccount(username, email, password, locale);
        retrievePostLoginInformation(loginResult);
    }

    public void performLogin(String usernameOrEmail, String password) throws InvalidLoginException {
        LoginResult loginResult = loginGateway.performLogin(usernameOrEmail, password);
        retrievePostLoginInformation(loginResult);
    }

    public Boolean performFacebookLogin(String facebookToken, String locale) throws InvalidLoginException {
        LoginResult loginResult = loginGateway.performFacebookLogin(facebookToken, locale);
        retrievePostLoginInformation(loginResult);
        return loginResult.isNewUser();
    }

    private void retrievePostLoginInformation(LoginResult loginResult) {
        storeSession(loginResult);
        String visibleEventId = loginResult.getUser().getIdWatchingStream();
        if (visibleEventId != null) {
            remoteStreamRepository.getStreamById(visibleEventId);
        }
        List<Nicer> nices = nicerRepository.getNices(loginResult.getUser().getIdUser());
        List<String> nicedIdShots = new ArrayList<>(nices.size());
        for (Nicer nice : nices) {
            nicedIdShots.add(nice.getIdShot());
        }
        //TODO save all nices
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

    public void sendPasswordResetEmail(String idUser, String loacale) throws IOException {
        resetPasswordEmailGateway.sendPasswordResetEmail(idUser, loacale);
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

    public void changePassword(String currentPassword, String newPassword, String locale)
      throws InvalidPasswordException {
        changePasswordGateway.changePassword(currentPassword, newPassword, locale);
    }

    public void removeSessionData() {
        removeSession();
        databaseUtils.clearDataOnLogout();
    }
}
