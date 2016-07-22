package com.shootr.mobile.domain.interactor.poll;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.poll.ServerPollRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class SharePollInteractorTest {

  public static final String ID_POLL = "id_poll";
  @Mock ServerPollRepository remotePollRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private SharePollInteractor sharePollInteractor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    sharePollInteractor =
        new SharePollInteractor(remotePollRepository, interactorHandler, postExecutionThread);
  }

  @Test public void shouldSharePollInRemoteRepository() throws Exception {
    sharePollInteractor.sharePoll(ID_POLL, callback, errorCallback);

    verify(remotePollRepository).sharePoll(anyString());
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsShootrException() throws Exception {
    doThrow(new ShootrException() {
    }).when(remotePollRepository).sharePoll(anyString());

    sharePollInteractor.sharePoll(ID_POLL, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyCompletedWhenSharePollInRemoteRepository() throws Exception {
    sharePollInteractor.sharePoll(ID_POLL, callback, errorCallback);

    verify(callback).onCompleted();
  }
}