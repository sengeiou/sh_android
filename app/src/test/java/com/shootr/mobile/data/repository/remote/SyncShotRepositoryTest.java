package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncShotRepositoryTest {

  private static final String ID_SHOT = "idShot";
  public static final long TIMESTAMP = 10L;
  private static final String ID_USER = "idUser";
  private static final StreamTimelineParameters PARAMETERS =
      StreamTimelineParameters.builder().build();
  private static final Integer LIMIT = 1;
  private static final Long DATE = 1L;
  private static String[] STREAM_TYPES = {
      "PUBLIC", "VIEWONLY"
  };
  private static final String[] SHOT_TYPES = { "COMMENT" };
  @Mock ShotDataSource remoteShotDataSource;
  @Mock ShotDataSource localShotDataSource;
  @Mock ShotEntityMapper shotEntityMapper;
  @Mock SyncTrigger syncTrigger;

  private SyncShotRepository syncShotRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    syncShotRepository =
        new SyncShotRepository(remoteShotDataSource, localShotDataSource, shotEntityMapper,
            syncTrigger);
  }

  @Test public void shouldGetRemoteShotWhenGetShotAndLocalShotIsNull() throws Exception {
    when(localShotDataSource.getShot(ID_SHOT, STREAM_TYPES, SHOT_TYPES)).thenReturn(null);

    syncShotRepository.getShot(ID_SHOT, STREAM_TYPES, SHOT_TYPES);

    verify(remoteShotDataSource).getShot(anyString(), anyArray(), anyArray());
  }

  @Test public void shouldGetRemoteShotEntityWhenHideShotAndLocalShotEntityIsNull()
      throws Exception {
    when(localShotDataSource.getShot(ID_SHOT, STREAM_TYPES, SHOT_TYPES)).thenReturn(null);
    when(remoteShotDataSource.getShot(ID_SHOT, STREAM_TYPES, SHOT_TYPES)).thenReturn(shotEntity());

    syncShotRepository.hideShot(ID_SHOT, TIMESTAMP, STREAM_TYPES, SHOT_TYPES);

    verify(remoteShotDataSource).getShot(anyString(), anyArray(), anyArray());
  }

  @Test public void shouldNotifyNeedSyncWhenHideShotAndThrowServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).when(remoteShotDataSource)
        .hideShot(ID_SHOT, TIMESTAMP);
    when(localShotDataSource.getShot(ID_SHOT, STREAM_TYPES, SHOT_TYPES)).thenReturn(shotEntity());

    syncShotRepository.hideShot(ID_SHOT, TIMESTAMP, STREAM_TYPES, SHOT_TYPES);

    verify(syncTrigger).notifyNeedsSync(any(SyncableRepository.class));
  }

  @Test public void shouldHideShotWhenDispatchSyncAndAlmostOnePendingEntities() throws Exception {
    when(localShotDataSource.getEntitiesNotSynchronized()).thenReturn(shotEntities());

    syncShotRepository.dispatchSync();

    verify(remoteShotDataSource, atLeastOnce()).hideShot(anyString(), anyLong());
  }

  @Test public void shouldNotHideShotWhenDispatchSyncAndZeroPendingEntities() throws Exception {
    when(localShotDataSource.getEntitiesNotSynchronized()).thenReturn(new ArrayList<ShotEntity>());

    syncShotRepository.dispatchSync();

    verify(remoteShotDataSource, never()).hideShot(anyString(), anyLong());
  }

  @Test public void shouldPutShotInRemoteWhenDataSourceWhenCallPutShot() throws Exception {
    syncShotRepository.putShot(shot());

    verify(remoteShotDataSource).putShot(any(ShotEntity.class));
  }

  @Test public void shouldPutShotsInLocalWhenGetShotsForStreamTimeline() throws Exception {
    syncShotRepository.getShotsForStreamTimeline(PARAMETERS);

    verify(localShotDataSource).putShots(anyList());
  }

  @Test public void shouldGetRepliesFromRemoteWhenCallGetReplies() throws Exception {
    syncShotRepository.getReplies(ID_SHOT, STREAM_TYPES, SHOT_TYPES);

    verify(remoteShotDataSource).getReplies(anyString(), anyArray(), anyArray());
  }

  @Test public void shouldGetRemoteStreamMediaShotsWhenCallGetMediaByIdStream() throws Exception {
    syncShotRepository.getMediaByIdStream(ID_SHOT, Collections.singletonList(ID_USER), TIMESTAMP,
        STREAM_TYPES, SHOT_TYPES);

    verify(remoteShotDataSource).getStreamMediaShots(anyString(), anyList(), anyLong(), anyArray(),
        anyArray());
  }

  @Test public void shouldGetRemoteShotsWhenCallGetShotsFromUser() throws Exception {
    syncShotRepository.getShotsFromUser(ID_USER, LIMIT, STREAM_TYPES, SHOT_TYPES);

    verify(remoteShotDataSource).getShotsFromUser(anyString(), anyInt(), anyArray(), anyArray());
  }

  @Test public void shouldPutParentsShotsInLocalWhenGetShotDetailAndHaveParents() throws Exception {
    when(remoteShotDataSource.getShotDetail(anyString(), anyArray(), anyArray())).thenReturn(
        shotDetail());

    syncShotRepository.getShotDetail(ID_SHOT, STREAM_TYPES, SHOT_TYPES);

    verify(localShotDataSource).putShots(anyList());
  }

  @Test public void shouldGetAllShotsFromUserFromRemoteWhenCallGetAllShotsFromUser()
      throws Exception {
    syncShotRepository.getAllShotsFromUser(ID_USER, STREAM_TYPES, SHOT_TYPES);

    verify(remoteShotDataSource).getAllShotsFromUser(anyString(), anyArray(), anyArray());
  }

  @Test public void shouldGetAllShotsFromUserAndDateFromRemoteWhenCallGetAllShotsFromUserAndDate()
      throws Exception {
    syncShotRepository.getAllShotsFromUserAndDate(ID_USER, DATE, STREAM_TYPES, SHOT_TYPES);

    verify(remoteShotDataSource).getAllShotsFromUserAndDate(anyString(), anyLong(), anyArray(),
        anyArray());
  }

  @Test public void shouldPutShotsInLocalWhenGetUserShotsForStreamTimeline() throws Exception {
    syncShotRepository.getUserShotsForStreamTimeline(PARAMETERS);

    verify(localShotDataSource).putShots(anyList());
  }

  private ShotDetailEntity shotDetail() {
    ShotDetailEntity shotDetailEntity = new ShotDetailEntity();
    shotDetailEntity.setParents(Collections.singletonList(shotEntity()));
    return shotDetailEntity;
  }

  private ShotEntity shotEntity() {
    ShotEntity shotEntity = new ShotEntity();
    shotEntity.setIdShot(ID_USER);
    shotEntity.setSynchronizedStatus(Synchronized.SYNC_NEW);
    return shotEntity;
  }

  private List<ShotEntity> shotEntities() {
    ArrayList<ShotEntity> shotEntities = new ArrayList<>();
    shotEntities.add(shotEntity());
    return shotEntities;
  }

  private Shot shot() {
    return new Shot();
  }

  protected String[] anyArray() {
    return any(String[].class);
  }
}
