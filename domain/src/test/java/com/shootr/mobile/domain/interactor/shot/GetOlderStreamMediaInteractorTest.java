package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;

public class GetOlderStreamMediaInteractorTest {

  private static final String ID_STREAM = "idStream";
  private static final Long TIME = 1L;
  @Mock ExternalShotRepository remoteShotRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Interactor.Callback<List<Shot>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private com.shootr.mobile.domain.interactor.shot.GetOlderStreamMediaInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor = new com.shootr.mobile.domain.interactor.shot.GetOlderStreamMediaInteractor(
        interactorHandler, postExecutionThread, remoteShotRepository);
  }

  @Test public void shouldNotifyResultsWhenGetMediaFromRemote() throws Exception {

    interactor.getOlderStreamMedia(ID_STREAM, TIME, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  private List<User> users() {
    return Collections.singletonList(new User());
  }
}
