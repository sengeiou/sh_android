package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.InvalidEmailConfirmationException;
import com.shootr.mobile.domain.exception.InvalidForgotPasswordException;
import com.shootr.mobile.domain.exception.InvalidLoginException;
import com.shootr.mobile.domain.exception.InvalidLoginMethodForFacebookException;
import com.shootr.mobile.domain.exception.InvalidLoginMethodForShootrException;
import com.shootr.mobile.domain.exception.InvalidPasswordException;
import com.shootr.mobile.domain.exception.MassiveRegisterErrorException;
import com.shootr.mobile.domain.exception.UnauthorizedRequestException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.user.ForgotPasswordResult;
import com.shootr.mobile.domain.model.user.LoginResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.DatabaseUtils;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.io.IOException;
import javax.inject.Inject;

public class ShootrUserService {

  private final UserRepository localUserRepository;
  private final SessionRepository sessionRepository;
  private final CreateAccountGateway createAccountGateway;
  private final LoginGateway loginGateway;
  private final ResetPasswordGateway resetPasswordGateway;
  private final ConfirmEmailGateway confirmEmailGateway;
  private final ExternalStreamRepository remoteStreamRepository;
  private final ChangePasswordGateway changePasswordGateway;
  private final ResetPasswordEmailGateway resetPasswordEmailGateway;
  private final DatabaseUtils databaseUtils;

  @Inject public ShootrUserService(@Local UserRepository localUserRepository,
      SessionRepository sessionRepository, CreateAccountGateway createAccountGateway,
      LoginGateway loginGateway, ResetPasswordGateway resetPasswordGateway,
      ChangePasswordGateway changePasswordGateway, ConfirmEmailGateway confirmEmailGateway,
      ExternalStreamRepository remoteStreamRepository,
      ResetPasswordEmailGateway resetPasswordEmailGateway, DatabaseUtils databaseUtils) {
    this.localUserRepository = localUserRepository;
    this.sessionRepository = sessionRepository;
    this.createAccountGateway = createAccountGateway;
    this.loginGateway = loginGateway;
    this.resetPasswordGateway = resetPasswordGateway;
    this.confirmEmailGateway = confirmEmailGateway;
    this.remoteStreamRepository = remoteStreamRepository;
    this.changePasswordGateway = changePasswordGateway;
    this.resetPasswordEmailGateway = resetPasswordEmailGateway;
    this.databaseUtils = databaseUtils;
  }

  public void createAccount(String username, String email, String password, boolean privacyAccepted, String locale)
      throws EmailAlreadyExistsException, UsernameAlreadyExistsException,
      MassiveRegisterErrorException {
    LoginResult loginResult =
        createAccountGateway.performCreateAccount(username, email, password, privacyAccepted, locale);
    retrievePostLoginInformation(loginResult);
  }

  public void performLogin(String usernameOrEmail, String password)
      throws InvalidLoginException, InvalidLoginMethodForShootrException {
    LoginResult loginResult = loginGateway.performLogin(usernameOrEmail, password);
    retrievePostLoginInformation(loginResult);
  }

  public Boolean performFacebookLogin(String facebookToken, String locale)
      throws InvalidLoginException, InvalidLoginMethodForFacebookException, MassiveRegisterErrorException {
    LoginResult loginResult = loginGateway.performFacebookLogin(facebookToken, locale);
    retrievePostLoginInformation(loginResult);
    return loginResult.isNewUser();
  }

  private void retrievePostLoginInformation(LoginResult loginResult) {
    storeSession(loginResult);
    String visibleEventId = loginResult.getUser().getIdWatchingStream();
    if (visibleEventId != null) {
      remoteStreamRepository.getStreamById(visibleEventId, StreamMode.TYPES_STREAM);
    }
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

  public void requestEmailConfirmation() {
    try {
      confirmEmailGateway.confirmEmail();
    } catch (InvalidEmailConfirmationException e) {
            /* no-op */
    }
  }

  public void changeEmail(String email)
      throws EmailAlreadyExistsException, EmailAlreadyConfirmedException,
      UnauthorizedRequestException {
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
