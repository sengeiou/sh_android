package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnfollowInteractorTest {
  public static final String ID_USER = "id_user";
  private UnfollowInteractor unfollowInteractor;
  @Mock FollowRepository localFollowRepository;
  @Mock FollowRepository remoteFollowRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock Interactor.CompletedCallback callback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    unfollowInteractor =
        new UnfollowInteractor(interactorHandler, postExecutionThread, localFollowRepository,
            remoteFollowRepository, remoteUserRepository);
  }

  @Test
  public void shouldUnfollowInLocal() throws Exception {
    unfollowInteractor.unfollow(ID_USER, callback);

    verify(localFollowRepository).unfollow(ID_USER);
  }

  @Test
  public void shouldUnfollowInRemote() throws Exception {
    unfollowInteractor.unfollow(ID_USER, callback);

    verify(remoteFollowRepository).unfollow(ID_USER);
  }

  @Test
  public void shouldUpdateUser() throws Exception {
    unfollowInteractor.unfollow(ID_USER, callback);

    verify(remoteUserRepository).getUserById(ID_USER);
  }

  @Test
  public void shouldNotifyCompleted() throws Exception {
    unfollowInteractor.unfollow(ID_USER, callback);

    verify(callback).onCompleted();
  }

  @Test
  public void shouldNotifyCompletedEvenIfExceptionIsThrown() throws Exception {
    when(remoteUserRepository.getUserById(ID_USER)).thenThrow(new ServerCommunicationException(new Throwable()));

    unfollowInteractor.unfollow(ID_USER, callback);

    verify(callback).onCompleted();
  }
}
