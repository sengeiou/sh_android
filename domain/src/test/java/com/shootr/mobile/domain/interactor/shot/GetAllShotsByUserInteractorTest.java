package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAllShotsByUserInteractorTest {

  private static final String USER_ID = "user_id";
  private GetAllShotsByUserInteractor getAllShotsByUserInteractor;
  private static String[] TYPES_SHOT = ShotType.TYPES_SHOWN;
  private static String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  @Mock InternalShotRepository localShotRepository;
  @Mock ExternalShotRepository remoteShotRepository;
  @Mock Interactor.Callback<List<Shot>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getAllShotsByUserInteractor =
        new GetAllShotsByUserInteractor(interactorHandler, postExecutionThread, localShotRepository,
            remoteShotRepository);
  }

  @Test public void shouldGetAllShotsFromUserInLocal() throws Exception {
    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(localShotRepository).getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT);
  }

  @Test public void shouldNotifyLoadedIfThereAreShotsFromUserInLocal() throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shots());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback, times(2)).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedIfThereIsAShotFromUserInLocal() throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shot());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback, times(2)).onLoaded(anyList());
  }

  @Test public void shouldGetAllShotsFromUserInRemote() throws Exception {
    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(remoteShotRepository).getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT);
  }

  @Test public void shouldNotifyLoadedIfThereAreShotsFromUserInRemote() throws Exception {
    when(remoteShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shots());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedIfThereIsAShotFromUserInRemote() throws Exception {
    when(remoteShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shot());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedTwoTimesIfThereAreShotsFromUserInRemoteAndLocal()
      throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shots());
    when(remoteShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shots());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback, times(2)).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedTwoTimesIfThereIsAShotFromUserInRemoteAndLocal()
      throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shot());
    when(remoteShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shot());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback, times(2)).onLoaded(anyList());
  }

  @Test public void shouldNotifyErrorWhenRemoteShotRepositoryThrowsShootrException()
      throws Exception {
    when(remoteShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenThrow(
        new ShootrException() {
        });

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyShotsFromLocalEvenIfInRemoteThrowsException() throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenReturn(
        shot());
    when(remoteShotRepository.getAllShotsFromUser(USER_ID, TYPES_STREAM, TYPES_SHOT)).thenThrow(
        new ShootrException() {
        });

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  private List<Shot> shot() {
    Shot shot = new Shot();
    shot.setPublishDate(new Date());

    return Collections.singletonList(shot);
  }

  private List<Shot> shots() {
    Shot shot = new Shot();
    shot.setPublishDate(new Date());

    Shot newerShot = new Shot();
    newerShot.setPublishDate(new Date());

    return Arrays.asList(shot, newerShot);
  }
}
