package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetBlogInteractorTest {

  private static final String COUNTRY = "country";
  private static final String LANGUAGE = "language";
  @Mock InteractorHandler interactorHandler;
  @Mock PostExecutionThread postExecutionThread;
  @Mock StreamRepository remoteStreamRepository;
  @Mock LocaleProvider localeProvider;
  @Mock Interactor.Callback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private GetBlogInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new GetBlogInteractor(interactorHandler, postExecutionThread, remoteStreamRepository,
            localeProvider);
  }

  @Test public void shouldNotifyWhenLoadRemoteBlogStream() throws Exception {
    when(localeProvider.getCountry()).thenReturn(COUNTRY);
    when(localeProvider.getLanguage()).thenReturn(LANGUAGE);
    when(remoteStreamRepository.getBlogStream(COUNTRY, LANGUAGE)).thenReturn(new Stream());

    interactor.obtainBlogStream(callback, errorCallback);

    verify(callback).onLoaded(any(Stream.class));
  }

  @Test public void shouldNotifyErrorWhenRemoteStreamThrowsServerComunicationException()
      throws Exception {
    when(remoteStreamRepository.getBlogStream(anyString(), anyString())).thenThrow(
        ServerCommunicationException.class);

    interactor.obtainBlogStream(callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }
}
