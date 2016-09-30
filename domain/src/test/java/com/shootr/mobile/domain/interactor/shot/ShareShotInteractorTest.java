package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ShareShotInteractorTest {

  public static final String ID_SHOT = "id_shot";
  private ShareShotInteractor shareShotInteractor;
  @Mock ExternalShotRepository remoteShotRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    shareShotInteractor =
        new ShareShotInteractor(remoteShotRepository, interactorHandler, postExecutionThread);
  }

  @Test public void shouldShareShotInRemoteRepository() throws Exception {
    shareShotInteractor.shareShot(ID_SHOT, callback, errorCallback);

    verify(remoteShotRepository).shareShot(anyString());
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsShootrException() throws Exception {
    doThrow(new ShootrException() {
    }).when(remoteShotRepository).shareShot(anyString());

    shareShotInteractor.shareShot(ID_SHOT, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyCompletedWhenShareShotInRemoteRepository() throws Exception {
    shareShotInteractor.shareShot(ID_SHOT, callback, errorCallback);

    verify(callback).onCompleted();
  }
}
