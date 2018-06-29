package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class CreateAccountInteractorTest {

  private static final String EMAIL = "email@email.com";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String WRONG_EMAIL = "wrongEmail";
  private static final boolean PRIVACY_CONFIRMED = true;
  @Mock ShootrUserService shootrUserService;
  @Mock LocaleProvider localeProvider;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private CreateAccountInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new CreateAccountInteractor(interactorHandler, postExecutionThread, shootrUserService,
            localeProvider);
  }

  @Test public void shouldNotifyLoadedWhenCreateAccountAndValidationsErrorIsEmpty()
      throws Exception {
    interactor.createAccount(EMAIL, USERNAME, PASSWORD, PRIVACY_CONFIRMED, callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenCreateAccountAndValidationsErrorIsNotEmpty()
      throws Exception {
    interactor.createAccount(WRONG_EMAIL, USERNAME, PASSWORD, PRIVACY_CONFIRMED, callback, errorCallback);

    verify(errorCallback).onError(any(DomainValidationException.class));
  }

  @Test public void shouldNotifyErrorWhenUserServiceThrowsEmailAlreadyExistException()
      throws Exception {
    doThrow(EmailAlreadyExistsException.class).when(shootrUserService)
        .createAccount(anyString(), anyString(), anyString(), anyBoolean(), anyString());

    interactor.createAccount(WRONG_EMAIL, USERNAME, PASSWORD, PRIVACY_CONFIRMED, callback, errorCallback);

    verify(errorCallback).onError(any(DomainValidationException.class));
  }

  @Test public void shouldNotifyErrorWhenUserServiceThrowsUsernameAlreadyExistsException()
      throws Exception {
    doThrow(UsernameAlreadyExistsException.class).when(shootrUserService)
        .createAccount(anyString(), anyString(), anyString(), anyBoolean(), anyString());

    interactor.createAccount(WRONG_EMAIL, USERNAME, PASSWORD, PRIVACY_CONFIRMED, callback, errorCallback);

    verify(errorCallback).onError(any(DomainValidationException.class));
  }

  @Test public void shouldNotifyErrorWhenUserServiceThrowsServerCommunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).when(shootrUserService)
        .createAccount(anyString(), anyString(), anyString(), anyBoolean(), anyString());

    interactor.createAccount(WRONG_EMAIL, USERNAME, PASSWORD, PRIVACY_CONFIRMED, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }
}
