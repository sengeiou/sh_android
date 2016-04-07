package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.data.repository.datasource.event.MuteDataSource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.exception.ServerCommunicationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SyncMuteRepositoryTest {

    public static final String ID_STREAM = "idStream";
    @Mock MuteDataSource localMuteDataSource;
    @Mock MuteDataSource remoteMuteDataSource;
    @Mock SyncTrigger syncTrigger;

    private SyncMuteRepository syncMuteRepository;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        syncMuteRepository = new SyncMuteRepository(localMuteDataSource, remoteMuteDataSource, syncTrigger);
    }

    @Test public void shouldRemoteSyncTriggerWhenMute() throws Exception {
        syncMuteRepository.mute(ID_STREAM);

        verify(syncTrigger).triggerSync();
    }

    @Test public void shouldNotifyNeedsSyncWhenMuteAndThrowServerComunicationException() throws Exception {
        doThrow(ServerCommunicationException.class).when(remoteMuteDataSource).mute(any(MuteStreamEntity.class));

        syncMuteRepository.mute(ID_STREAM);

        verify(syncTrigger).notifyNeedsSync(any(SyncableRepository.class));
    }

    @Test public void shouldSyncTriggerWhenUnMute() throws Exception {
        syncMuteRepository.unmute(ID_STREAM);

        verify(syncTrigger).triggerSync();
    }

    @Test public void shouldRemoteUnmuteWhenDispatchSyncAndAlmostOnePendingEntitySyncDeleted() throws Exception {
        when(localMuteDataSource.getEntitiesNotSynchronized()).thenReturn(muteStreamEntities());

        syncMuteRepository.dispatchSync();

        verify(remoteMuteDataSource, atLeastOnce()).unmute(anyString());
    }

    @Test public void shouldRemoteMuteWhenDispatchSyncAndAlmostOnePendingEntityIsNotSyncDeleted() throws Exception {
        when(localMuteDataSource.getEntitiesNotSynchronized()).thenReturn(muteStreamEntities());

        syncMuteRepository.dispatchSync();

        verify(remoteMuteDataSource).mute(any(MuteStreamEntity.class));
    }

    private MuteStreamEntity muteStreamEntity(){
        MuteStreamEntity muteStreamEntity = new MuteStreamEntity();
        muteStreamEntity.setIdStream(ID_STREAM);
        muteStreamEntity.setSynchronizedStatus(LocalSynchronized.SYNC_DELETED);
        return muteStreamEntity;
    }

    private MuteStreamEntity muteStreamEntitySyncNew(){
        MuteStreamEntity muteStreamEntity = new MuteStreamEntity();
        muteStreamEntity.setIdStream(ID_STREAM);
        muteStreamEntity.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        return muteStreamEntity;
    }

    private List<MuteStreamEntity> muteStreamEntities(){
        ArrayList<MuteStreamEntity> muteStreamEntities = new ArrayList<>();
        muteStreamEntities.add(muteStreamEntity());
        muteStreamEntities.add(muteStreamEntitySyncNew());
        return  muteStreamEntities;
    }
}
