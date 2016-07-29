package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetOlderAllShotsByUserInteractorTest {

  public static final String USER_ID = "user_id";
  public static final long CURRENT_OLDEST_DATE = 0L;
  private static String[] TYPES_SHOT = ShotType.TYPES_SHOWN;
  private static String[] TYPES_STREAM = StreamMode.TYPES_STREAM;
  @Mock ShotRepository remoteShotRepository;
  private GetOlderAllShotsByUserInteractor getOlderAllShotsByUserInteractor;
  @Mock Interactor.Callback<List<Shot>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getOlderAllShotsByUserInteractor =
        new GetOlderAllShotsByUserInteractor(interactorHandler, postExecutionThread,
            remoteShotRepository);
  }

  @Test public void shouldGetAllShotsFromUserAndDateFromRemoteRepository() throws Exception {
    getOlderAllShotsByUserInteractor.loadAllShots(USER_ID, CURRENT_OLDEST_DATE, callback,
        errorCallback);

    verify(remoteShotRepository).getAllShotsFromUserAndDate(USER_ID, CURRENT_OLDEST_DATE,
        TYPES_STREAM, TYPES_SHOT);
  }

  @Test public void shouldNotifyShotsFromUserAndDateFromRemoteRepository() throws Exception {
    when(remoteShotRepository.getAllShotsFromUserAndDate(USER_ID, CURRENT_OLDEST_DATE, TYPES_STREAM,
        TYPES_SHOT)).thenReturn(shots());

    getOlderAllShotsByUserInteractor.loadAllShots(USER_ID, CURRENT_OLDEST_DATE, callback,
        errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyShotFromUserAndDateFromRemoteRepository() throws Exception {
    when(remoteShotRepository.getAllShotsFromUserAndDate(USER_ID, CURRENT_OLDEST_DATE, TYPES_STREAM,
        TYPES_SHOT)).thenReturn(shot());

    getOlderAllShotsByUserInteractor.loadAllShots(USER_ID, CURRENT_OLDEST_DATE, callback,
        errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyErrorInRemoteThrowsShootrException() throws Exception {
    when(remoteShotRepository.getAllShotsFromUserAndDate(USER_ID, CURRENT_OLDEST_DATE, TYPES_STREAM,
        TYPES_SHOT)).thenThrow(new ShootrException() {
    });

    getOlderAllShotsByUserInteractor.loadAllShots(USER_ID, CURRENT_OLDEST_DATE, callback,
        errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
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
