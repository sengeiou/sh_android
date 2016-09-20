package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IncrementReplyCountShotInteractorTest {

  public static final String ID_SHOT = "ID_SHOT";
  private static String[] TYPES_SHOT = ShotType.TYPES_SHOWN;
  private static String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  private IncrementReplyCountShotInteractor incrementReplyCountShotInteractor;
  @Mock ShotRepository localShotRepository;
  @Mock ShotRepository remoteShotRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
    incrementReplyCountShotInteractor =
        new IncrementReplyCountShotInteractor(interactorHandler, postExecutionThread,
            localShotRepository, remoteShotRepository);
  }

  @Test public void shouldObtainLocalShotByIdShot() throws Exception {
    when(localShotRepository.getShot(ID_SHOT, TYPES_STREAM, TYPES_SHOT)).thenReturn(getShot());

    incrementReplyCountShotInteractor.incrementReplyCount(ID_SHOT, callback, errorCallback);

    verify(localShotRepository).getShot(ID_SHOT, TYPES_STREAM, TYPES_SHOT);
  }

  @Test public void shouldObtainRemoteShotIfLocalShotIsNull() throws Exception {
    when(remoteShotRepository.getShot(ID_SHOT, TYPES_STREAM, TYPES_SHOT)).thenReturn(getShot());

    incrementReplyCountShotInteractor.incrementReplyCount(ID_SHOT, callback, errorCallback);

    verify(remoteShotRepository).getShot(ID_SHOT, TYPES_STREAM, TYPES_SHOT);
  }

  @Test public void shouldIncrementReplyCountWhenShotObtainedFromLocalRepository()
      throws Exception {
    Shot shot = getShot();
    when(localShotRepository.getShot(ID_SHOT, TYPES_STREAM, TYPES_SHOT)).thenReturn(shot);

    incrementReplyCountShotInteractor.incrementReplyCount(ID_SHOT, callback, errorCallback);

    verify(localShotRepository).putShot(shot);
  }

  @Test public void shouldIncrementReplyCountWhenShotObtainedFromRemoteRepository()
      throws Exception {
    Shot shot = getShot();
    when(remoteShotRepository.getShot(ID_SHOT, TYPES_STREAM, TYPES_SHOT)).thenReturn(shot);

    incrementReplyCountShotInteractor.incrementReplyCount(ID_SHOT, callback, errorCallback);

    verify(localShotRepository).putShot(shot);
  }

  @Test public void shouldNotifyCompletedWhenReplyCountIncremented() throws Exception {
    Shot shot = getShot();
    when(localShotRepository.getShot(ID_SHOT, TYPES_STREAM, TYPES_SHOT)).thenReturn(shot);

    incrementReplyCountShotInteractor.incrementReplyCount(ID_SHOT, callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test public void shouldNotifyErrorIfGetRemoteShotThrowsServerCommunicationException()
      throws Exception {
    doThrow(new ShootrException() {
    }).when(remoteShotRepository).getShot(ID_SHOT, TYPES_STREAM, TYPES_SHOT);

    incrementReplyCountShotInteractor.incrementReplyCount(ID_SHOT, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  private Shot getShot() {
    Shot shot = new Shot();
    shot.setReplyCount(0L);
    return shot;
  }
}
