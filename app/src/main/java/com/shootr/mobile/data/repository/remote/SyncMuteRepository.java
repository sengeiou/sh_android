package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.LocalSynchronized;
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
import timber.log.Timber;

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
        MuteStreamEntity mute = createMute(idStream);
        try {
            remoteMuteDataSource.mute(mute);
            mute.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
            localMuteDataSource.mute(mute);
            syncTrigger.triggerSync();
            dispatchSync();
        } catch (ServerCommunicationException e) {
            queueUpload(mute, e);
        }
    }

    @Override public List<String> getMutedIdStreams() {
        syncTrigger.triggerSync();
        dispatchSync();
        List<MuteStreamEntity> muteStreamEntities = remoteMuteDataSource.getMutedStreamEntities();
        for (MuteStreamEntity muteStreamEntity : muteStreamEntities) {
            muteStreamEntity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
        }
        localMuteDataSource.putMuteds(muteStreamEntities);
        List<String> mutedIdStreams = new ArrayList<>(muteStreamEntities.size());
        for (MuteStreamEntity muteStreamEntity : muteStreamEntities) {
            mutedIdStreams.add(muteStreamEntity.getIdStream());
        }
        return mutedIdStreams;
    }

    @Override public void unmute(String idStream) {
        try {
            syncTrigger.triggerSync();
            dispatchSync();
            syncUnmute(idStream);
        } catch (ServerCommunicationException e) {
            deleteMute(idStream);
        }
    }

    @Override public void dispatchSync() {
        List<MuteStreamEntity> pendingEntities = localMuteDataSource.getEntitiesNotSynchronized();
        for (MuteStreamEntity entity : pendingEntities) {
            if (LocalSynchronized.SYNC_DELETED.equals(entity.getSynchronizedStatus())) {
                syncUnmute(entity.getIdStream());
            } else {
                syncEntities(entity);
            }
        }
    }

    public void syncUnmute(String idStream) {
        try {
            remoteMuteDataSource.unmute(idStream);
            localMuteDataSource.unmute(idStream);
        } catch (ServerCommunicationException error) {
            /* no-op*/
        }
    }

    private void queueUpload(MuteStreamEntity muteStreamEntity, ServerCommunicationException reason) {
        Timber.w(reason,
          "Mute upload queued: idStream %s",
          muteStreamEntity.getIdStream());
        prepareEntityForSynchronization(muteStreamEntity);
        syncTrigger.notifyNeedsSync(this);
    }

    private void prepareEntityForSynchronization(MuteStreamEntity muteStreamEntity) {
        if (!isReadyForSync(muteStreamEntity)) {
            muteStreamEntity.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        }
        localMuteDataSource.mute(muteStreamEntity);
    }

    private boolean isReadyForSync(MuteStreamEntity muteStreamEntity) {
        return LocalSynchronized.SYNC_UPDATED.equals(muteStreamEntity.getSynchronizedStatus())
          || LocalSynchronized.SYNC_NEW.equals(muteStreamEntity.getSynchronizedStatus())
          || LocalSynchronized.SYNC_DELETED.equals(muteStreamEntity.getSynchronizedStatus());
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
        try {
            remoteMuteDataSource.mute(entity);
            entity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
            localMuteDataSource.mute(entity);
        } catch (ServerCommunicationException error) {
            /* no-op*/
        }
    }
}
