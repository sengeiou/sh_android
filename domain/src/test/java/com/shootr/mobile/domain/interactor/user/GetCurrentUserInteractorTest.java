package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetCurrentUserInteractorTest {
  public static final String ID_USER = "id_user";
  private GetCurrentUserInteractor getCurrentUserInteractor;
  @Mock SessionRepository sessionRepository;
  @Mock UserRepository localUserRepository;
  @Mock Interactor.Callback<User> callback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExectuionThread = new TestPostExecutionThread();
    getCurrentUserInteractor =
        new GetCurrentUserInteractor(interactorHandler, postExectuionThread, sessionRepository,
            localUserRepository);
  }

  @Test
  public void shouldNotifyLoadedCurrentUser() throws Exception {
    User user = new User();
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getUserById(ID_USER)).thenReturn(user);

    getCurrentUserInteractor.getCurrentUser(callback);

    verify(callback).onLoaded(user);
  }
}
