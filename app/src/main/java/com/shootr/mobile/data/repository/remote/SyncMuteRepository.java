package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.data.entity.Synchronized;
import com.shootr.mobile.data.repository.datasource.event.MuteDataSource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.MuteRepository;
import com.shootr.mobile.domain.repository.Remote;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class SyncMuteRepository implements MuteRepository, SyncableRepository {

    private final MuteDataSource localMuteDataSource;
    private final MuteDataSource remoteMuteDataSource;
    private final SyncTrigger syncTrigger;

    @Inject public SyncMuteRepository(@Local MuteDataSource localMuteDataSource, @Remote MuteDataSource remoteMuteDataSource,
      SyncTrigger syncTrigger) {
        this.localMuteDataSource = localMuteDataSource;
        this.remoteMuteDataSource = remoteMuteDataSource;
        this.syncTrigger = syncTrigger;
    }

    @Override public void mute(String idStream) {
        MuteStreamEntity followEntity = createMute(idStream);
        try {
            remoteMuteDataSource.mute(followEntity);
            followEntity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
            localMuteDataSource.mute(followEntity);
        } catch (ServerCommunicationException e) {
            followEntity.setSynchronizedStatus(Synchronized.SYNC_UPDATED);
            localMuteDataSource.mute(followEntity);
            syncTrigger.notifyNeedsSync(this);
        }
    }

    @Override public void unmute(String idStream) {
        try {
            remoteMuteDataSource.unmute(idStream);
            localMuteDataSource.unmute(idStream);
        } catch (ServerCommunicationException e) {
            deleteMute(idStream);
        }
    }

    @Override public List<String> getMutedIdStreams() {
        List<MuteStreamEntity> muteStreamEntities = remoteMuteDataSource.getMutedStreamEntities();
        localMuteDataSource.putMuteds(muteStreamEntities);
        List<String> bannedIdUsers = new ArrayList<>(muteStreamEntities.size());
        for (MuteStreamEntity muteStreamEntity : muteStreamEntities) {
            bannedIdUsers.add(muteStreamEntity.getIdStream());
        }
        return bannedIdUsers;
    }

    @Override public void dispatchSync() {
        List<MuteStreamEntity> pendingEntities = localMuteDataSource.getEntitiesNotSynchronized();
        for (MuteStreamEntity entity : pendingEntities) {
            if (Synchronized.SYNC_DELETED.equals(entity.getSynchronizedStatus())) {
                remoteMuteDataSource.unmute(entity.getIdStream());
                localMuteDataSource.unmute(entity.getIdStream());
            } else {
                syncEntities(entity);
            }
        }
    }

    private void deleteMute(String idStream) {
        MuteStreamEntity deletedMute = createMute(idStream);
        deletedMute.setSynchronizedStatus(Synchronized.SYNC_DELETED);
        localMuteDataSource.mute(deletedMute);
        syncTrigger.notifyNeedsSync(this);
    }

    private MuteStreamEntity createMute(String idStream) {
        MuteStreamEntity muteStreamEntity = new MuteStreamEntity();
        muteStreamEntity.setIdStream(idStream);
        Date now = new Date();
        muteStreamEntity.setBirth(now);
        muteStreamEntity.setModified(now);
        return muteStreamEntity;
    }

    private void syncEntities(MuteStreamEntity entity) {
        remoteMuteDataSource.mute(entity);
        entity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
        localMuteDataSource.mute(entity);
    }
}
