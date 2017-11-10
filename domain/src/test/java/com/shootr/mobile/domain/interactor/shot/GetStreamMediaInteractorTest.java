package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetStreamMediaInteractorTest {

  private static final String ID_STREAM = "idStream";
  @Mock ExternalShotRepository remoteShotRepository;
  @Mock InternalShotRepository localShotRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock UserRepository localUserRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Interactor.Callback<List<Shot>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private com.shootr.mobile.domain.interactor.shot.GetStreamMediaInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new com.shootr.mobile.domain.interactor.shot.GetStreamMediaInteractor(interactorHandler,
            postExecutionThread, remoteShotRepository, localShotRepository);
  }

  @Test public void shouldNotifyMediaWhenGetShotsFromRepository() throws Exception {
    interactor.getStreamMedia(ID_STREAM, callback, errorCallback);

    verify(callback, atLeastOnce()).onLoaded(anyList());
  }

  private List<User> users() {
    return Collections.singletonList(new User());
  }
}
