package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.mapper.ShotEntityMapper;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
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
    @Mock ShotDataSource remoteShotDataSource;
    @Mock ShotDataSource localShotDataSource;
    @Mock ShotEntityMapper shotEntityMapper;
    @Mock SyncTrigger syncTrigger;

    private SyncShotRepository syncShotRepository;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        syncShotRepository =
          new SyncShotRepository(remoteShotDataSource, localShotDataSource, shotEntityMapper, syncTrigger);
    }

    @Test public void shouldGetRemoteShotWhenGetShotAndLocalShotIsNull() throws Exception {
        when(localShotDataSource.getShot(ID_SHOT)).thenReturn(null);

        syncShotRepository.getShot(ID_SHOT);

        verify(remoteShotDataSource).getShot(anyString());
    }

    @Test public void shouldGetRemoteShotEntityWhenHideShotAndLocalShotEntityIsNull() throws Exception {
        when(localShotDataSource.getShot(ID_SHOT)).thenReturn(null);
        when(remoteShotDataSource.getShot(ID_SHOT)).thenReturn(shotEntity());

        syncShotRepository.hideShot(ID_SHOT, TIMESTAMP);

        verify(remoteShotDataSource).getShot(anyString());
    }

    @Test public void shouldNotifyNeedSyncWhenHideShotAndThrowServerComunicationException() throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteShotDataSource).hideShot(ID_SHOT, TIMESTAMP);
        when(localShotDataSource.getShot(ID_SHOT)).thenReturn(shotEntity());

        syncShotRepository.hideShot(ID_SHOT, TIMESTAMP);

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

    private ShotEntity shotEntity(){
        ShotEntity shotEntity = new ShotEntity();
        shotEntity.setIdShot(ID_USER);
        shotEntity.setSynchronizedStatus(Synchronized.SYNC_NEW);
        return shotEntity;
    }

    private List<ShotEntity> shotEntities(){
        ArrayList<ShotEntity> shotEntities = new ArrayList<>();
        shotEntities.add(shotEntity());
        return shotEntities;
    }
}
