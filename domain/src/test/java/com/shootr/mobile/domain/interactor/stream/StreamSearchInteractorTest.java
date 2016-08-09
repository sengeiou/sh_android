package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.StreamSearchRepository;
import com.shootr.mobile.domain.service.stream.WatchingStreamService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StreamSearchInteractorTest {

  private static final String INVALID_QUERY = "aa";
  private static final String QUERY = "query";
  @Mock StreamSearchRepository streamSearchRepository;
  @Mock LocaleProvider localeProvider;
  @Mock StreamSearchInteractor.Callback callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock WatchingStreamService watchingStreamService;

  private StreamSearchInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new StreamSearchInteractor(interactorHandler, streamSearchRepository, watchingStreamService,
            postExecutionThread,
            localeProvider);
  }

  @Test public void shouldNotifyErrorWhenIsNotValidSearchQuery() throws Exception {
    interactor.searchStreams(INVALID_QUERY, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrValidationException.class));
  }

  @Test public void shouldNotifySuccessfulSearchWhenIsValidQuery() throws Exception {
    when(streamSearchRepository.getStreams(anyString(), anyString(), anyArray())).thenReturn(
        streams());

    interactor.searchStreams(QUERY, callback, errorCallback);

    verify(callback).onLoaded(any(StreamSearchResultList.class));
  }

  private List<StreamSearchResult> streams() {
    return Collections.singletonList(new StreamSearchResult());
  }

  private String[] anyArray() {
    return any(String[].class);
  }
}
