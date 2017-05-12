package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.user.LoginResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.DatabaseUtils;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.nice.NicerRepository;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.MuteRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShootrUserServiceLoginTest {

  private static final String CURRENT_USER_ID = "user_id";
  private static final String WATCHING_STREAM_ID = "watching_id";
  private static final String USERNAME_OR_EMAIL_STUB = "username_or_email";
  private static final String SESSION_TOKEN_STUB = "sessionToken";

  private static final String PASSWORD_STUB = "password";
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  @Mock UserRepository localUserRepository;
  @Mock SessionRepository sessionRepository;
  @Mock CreateAccountGateway createAccountGateway;
  @Mock LoginGateway loginGateway;
  @Mock ResetPasswordGateway resetPasswordGateway;
  @Mock ConfirmEmailGateway confirmEmailGateway;
  @Mock ExternalStreamRepository remoteStreamRepository;
  @Mock ChangePasswordGateway changePasswordGateway;
  @Mock UserRepository remoteUserRepository;
  @Mock ResetPasswordEmailGateway resetPasswordEmailGateway;
  @Mock DatabaseUtils databaseUtils;
  @Mock NicerRepository nicerRepository;
  @Mock ExternalFavoriteRepository favoriteRepository;
  @Mock MuteRepository muteRepository;

  private ShootrUserService shootrUserService;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    shootrUserService =
        new ShootrUserService(localUserRepository, sessionRepository, createAccountGateway,
            loginGateway, resetPasswordGateway, changePasswordGateway, confirmEmailGateway,
            remoteStreamRepository, remoteUserRepository, resetPasswordEmailGateway, databaseUtils,
            nicerRepository, muteRepository, favoriteRepository);
  }

  @Test public void shouldCreateSessionWhenLoginCorrect() throws Exception {
    when(loginGateway.performLogin(anyString(), anyString())).thenReturn(loginResultCorrect());

    shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);

    verify(sessionRepository).createSession(CURRENT_USER_ID, SESSION_TOKEN_STUB, currentUser());
  }

  @Test public void shouldDownloadStreamIfUserHasStreamsWhenLoginCorrect() throws Exception {
    when(loginGateway.performLogin(anyString(), anyString())).thenReturn(loginResultWithStream());

    shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);

    verify(remoteStreamRepository).getStreamById(WATCHING_STREAM_ID, TYPES_STREAM);
  }

  @Test public void shouldNotDownloadAnyStreamIfUserHasNotStreamsWhenLoginCorrect()
      throws Exception {
    when(loginGateway.performLogin(anyString(), anyString())).thenReturn(
        loginResultWithoutStream());
    shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);
    verify(remoteStreamRepository, never()).getStreamById(anyString(), anyArray());
  }

  @Test public void shouldDownloadPeopleIfUserHasStreamsWhenLoginCorrect() throws Exception {
    when(loginGateway.performLogin(anyString(), anyString())).thenReturn(loginResultWithStream());
    shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);
    verify(remoteUserRepository).getPeople();
  }

  @Test public void shouldDownlaodPeopleIfUserHasNotStreamsWhenLoginCorrect() throws Exception {
    when(loginGateway.performLogin(anyString(), anyString())).thenReturn(
        loginResultWithoutStream());
    shootrUserService.performLogin(USERNAME_OR_EMAIL_STUB, PASSWORD_STUB);
    verify(remoteUserRepository).getPeople();
  }

  private LoginResult loginResultWithoutStream() {
    User user = currentUserWithoutStreams();
    return new LoginResult(user, SESSION_TOKEN_STUB);
  }

  private User currentUserWithoutStreams() {
    User user = new User();
    user.setIdUser(String.valueOf(CURRENT_USER_ID));
    return user;
  }

  private LoginResult loginResultWithStream() {
    User user = currentUser();
    user.setIdWatchingStream(String.valueOf(WATCHING_STREAM_ID));
    return new LoginResult(user, SESSION_TOKEN_STUB);
  }

  private LoginResult loginResultCorrect() {
    return new LoginResult(currentUser(), SESSION_TOKEN_STUB);
  }

  private User currentUser() {
    User user = new User();
    user.setIdUser(String.valueOf(CURRENT_USER_ID));
    user.setIdWatchingStream(WATCHING_STREAM_ID);
    return user;
  }

  private String[] anyArray() {
    return any(String[].class);
  }
}