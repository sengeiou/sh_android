package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.NiceNotMarkedException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.nice.InternalNiceShotRepository;
import com.shootr.mobile.domain.repository.nice.NiceShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnmarkNiceShotInteractorTest {

  private static final String SHOT_ID = "shot_id";
  private static final Long STREAM_ID = 2L;
  private static final String STREAM_TITLE = "title";
  private static final Long SHOT_USER_ID = 3L;
  private static final String SHOT_USERNAME = "username";

  @Mock InternalNiceShotRepository niceShotRepository;
  @Mock ShotRepository localShotRepository;
  @Mock ShotRepository remoteShotRepository;
  @Mock NiceShotRepository remoteNiceShotRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private UnmarkNiceShotInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor =
        new UnmarkNiceShotInteractor(interactorHandler, postExecutionThread, niceShotRepository,
            remoteNiceShotRepository, localShotRepository, remoteShotRepository);
  }

  @Test public void shouldNotifyCallbackAfterSendingToServer() throws Exception {
    setupLocalShot();

    interactor.unmarkNiceShot(SHOT_ID, callback, errorCallback);

    InOrder inOrder = inOrder(callback, remoteNiceShotRepository);
    inOrder.verify(remoteNiceShotRepository).unmark(SHOT_ID);
    inOrder.verify(callback).onCompleted();
  }

  @Test public void shouldDecrementNiceCountWhenMarkNiceInLocal() throws Exception {
    setupLocalShot();

    interactor.unmarkNiceShot(SHOT_ID, callback, errorCallback);

    Shot shot = shot();
    shot.setNiceCount(shot.getNiceCount() - 1);
    verify(localShotRepository).putShot(shot);
  }

  @Test public void shouldNotifyCallbackWhenServiceFails() throws Exception {
    setupLocalShot();
    doThrow(new ServerCommunicationException(null)).when(remoteNiceShotRepository)
        .unmark(anyString());

    interactor.unmarkNiceShot(SHOT_ID, callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenRepositoryFailsWithNiceNotMarked() throws Exception {
    setupLocalShot();
    doThrow(new NiceNotMarkedException()).when(niceShotRepository).unmark(anyString());

    interactor.unmarkNiceShot(SHOT_ID, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  private void setupLocalShot() {
    when(localShotRepository.getShot(anyString(), anyArray(), anyArray())).thenReturn(shot());
  }

  private String[] anyArray() {
    return any(String[].class);
  }

  private Shot shot() {
    Shot shot = new Shot();
    shot.setIdShot(SHOT_ID);
    shot.setStreamInfo(streamInfo());
    shot.setUserInfo(userInfo());
    shot.setType(ShotType.COMMENT);
    shot.setNiceCount(1);
    return shot;
  }

  private Shot.ShotStreamInfo streamInfo() {
    Shot.ShotStreamInfo shotStreamInfo = new Shot.ShotStreamInfo();
    shotStreamInfo.setIdStream(String.valueOf(STREAM_ID));
    shotStreamInfo.setStreamTitle(STREAM_TITLE);
    return shotStreamInfo;
  }

  private Shot.ShotUserInfo userInfo() {
    Shot.ShotUserInfo shotUserInfo = new Shot.ShotUserInfo();
    shotUserInfo.setIdUser(String.valueOf(SHOT_USER_ID));
    shotUserInfo.setUsername(SHOT_USERNAME);
    return shotUserInfo;
  }
}
