package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.InvalidLoginException;
import com.shootr.mobile.domain.exception.ShootrException;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PerformFacebookLoginInteractorTest {
  public static final String TOKEN = "token";
  public static final String LOCALE = "LOCALE";
  private PerformFacebookLoginInteractor performFacebookLoginInteractor;
  @Mock ShootrUserService shootrUserService;
  @Mock LocaleProvider localeProvider;
  @Mock Interactor.Callback<Boolean> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    performFacebookLoginInteractor =
        new PerformFacebookLoginInteractor(interactorHandler, postExecutionThread,
            shootrUserService, localeProvider);
  }

  @Test
  public void shouldGetLocaleFromLocaleProvider() throws Exception {
    performFacebookLoginInteractor.attempLogin(TOKEN, callback, errorCallback);

    verify(localeProvider).getLocale();
  }

  @Test
  public void shouldPerformLogin() throws Exception {
    performFacebookLoginInteractor.attempLogin(TOKEN, callback, errorCallback);

    verify(shootrUserService).performFacebookLogin(anyString(), anyString());
  }

  @Test
  public void shouldNotifyErrorWhenServiceThrowsInvalidLoginException() throws Exception {
    when(localeProvider.getLocale()).thenReturn(LOCALE);
    doThrow(new InvalidLoginException(new Throwable())).when(shootrUserService).performFacebookLogin(TOKEN,
        LOCALE);

    performFacebookLoginInteractor.attempLogin(TOKEN, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test
  public void shouldNotifyErrorWhenServiceThrowsUnknownError() throws Exception {
    when(localeProvider.getLocale()).thenReturn(LOCALE);
    doThrow(new ShootrException() {
    }).when(shootrUserService).performFacebookLogin(TOKEN,
        LOCALE);

    performFacebookLoginInteractor.attempLogin(TOKEN, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }
}
