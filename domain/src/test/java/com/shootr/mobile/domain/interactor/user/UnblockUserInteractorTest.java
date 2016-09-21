package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class UnblockUserInteractorTest {
  public static final String ID_USER = "id_user";
  private UnblockUserInteractor unblockUserInteractor;
  @Mock FollowRepository localFollowRepository;
  @Mock FollowRepository remoteFollowRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    unblockUserInteractor =
        new UnblockUserInteractor(interactorHandler, postExecutionThread, localFollowRepository,
            remoteFollowRepository);
  }

  @Test
  public void shouldUnblockInRemote() throws Exception {
    unblockUserInteractor.unblock(ID_USER, callback, errorCallback);

    verify(remoteFollowRepository).unblock(ID_USER);
  }

  @Test
  public void shouldUnblockInLocal() throws Exception {
    unblockUserInteractor.unblock(ID_USER, callback, errorCallback);

    verify(localFollowRepository).unblock(ID_USER);
  }

  @Test
  public void shouldNotifyCompleted() throws Exception {
    unblockUserInteractor.unblock(ID_USER, callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test
  public void shouldNotifyErrorWhenRemoteRepositoryThrowsServerCommunicationException()
      throws Exception {
    doThrow(new ServerCommunicationException(new Throwable())).when(remoteFollowRepository)
        .unblock(ID_USER);

    unblockUserInteractor.unblock(ID_USER, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }
}
