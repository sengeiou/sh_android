package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ShotRepository;
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
  @Mock ShotRepository localShotRepository;
  @Mock ShotRepository remoteShotRepository;
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

    verify(localShotRepository).getAllShotsFromUser(USER_ID);
  }

  @Test public void shouldNotifyLoadedIfThereAreShotsFromUserInLocal() throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shots());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback, times(2)).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedIfThereIsAShotFromUserInLocal() throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shot());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback, times(2)).onLoaded(anyList());
  }

  @Test public void shouldGetAllShotsFromUserInRemote() throws Exception {
    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(remoteShotRepository).getAllShotsFromUser(USER_ID);
  }

  @Test public void shouldNotifyLoadedIfThereAreShotsFromUserInRemote() throws Exception {
    when(remoteShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shots());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedIfThereIsAShotFromUserInRemote() throws Exception {
    when(remoteShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shot());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedTwoTimesIfThereAreShotsFromUserInRemoteAndLocal()
      throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shots());
    when(remoteShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shots());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback, times(2)).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedTwoTimesIfThereIsAShotFromUserInRemoteAndLocal()
      throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shot());
    when(remoteShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shot());

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(callback, times(2)).onLoaded(anyList());
  }

  @Test public void shouldNotifyErrorWhenRemoteShotRepositoryThrowsShootrException()
      throws Exception {
    when(remoteShotRepository.getAllShotsFromUser(USER_ID)).thenThrow(new ShootrException() {
    });

    getAllShotsByUserInteractor.loadAllShots(USER_ID, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyShotsFromLocalEvenIfInRemoteThrowsException() throws Exception {
    when(localShotRepository.getAllShotsFromUser(USER_ID)).thenReturn(shot());
    when(remoteShotRepository.getAllShotsFromUser(USER_ID)).thenThrow(new ShootrException() {
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
