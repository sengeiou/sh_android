package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.mapper.FavoriteEntityMapper;
import com.shootr.android.data.repository.datasource.event.FavoriteDataSource;
import com.shootr.android.data.repository.sync.SyncTrigger;
import com.shootr.android.data.repository.sync.SyncableFavoriteEntityFactory;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncFavoriteRepository implements FavoriteRepository, SyncableRepository {

    private final FavoriteDataSource remoteFavoriteDataSource;
    private final FavoriteDataSource localFavoriteDataSource;
    private final FavoriteEntityMapper favoriteEntityMapper;
    private final SyncableFavoriteEntityFactory syncableFavoriteEntityFactory;
    private final SyncTrigger syncTrigger;

    @Inject
    public SyncFavoriteRepository(@Remote FavoriteDataSource remoteFavoriteDataSource,
      @Local FavoriteDataSource localFavoriteDataSource,
      FavoriteEntityMapper favoriteEntityMapper,
      SyncableFavoriteEntityFactory syncableFavoriteEntityFactory,
      SyncTrigger syncTrigger) {
        this.remoteFavoriteDataSource = remoteFavoriteDataSource;
        this.localFavoriteDataSource = localFavoriteDataSource;
        this.favoriteEntityMapper = favoriteEntityMapper;
        this.syncableFavoriteEntityFactory = syncableFavoriteEntityFactory;
        this.syncTrigger = syncTrigger;
    }

    @Override
    public void putFavorite(Favorite favorite) {
        FavoriteEntity updatedOrNewEntity = syncableFavoriteEntityFactory.updatedOrNewEntity(favorite);
        try {
            FavoriteEntity remoteFavoriteEntity = remoteFavoriteDataSource.putFavorite(updatedOrNewEntity);
            markEntitySynchronized(remoteFavoriteEntity);
            localFavoriteDataSource.putFavorite(remoteFavoriteEntity);
            syncTrigger.triggerSync();
        } catch (ServerCommunicationException error) {
            queueUpload(updatedOrNewEntity, error);
        }
    }

    @Override
    public List<Favorite> getFavorites() {
        syncTrigger.triggerSync();
        List<FavoriteEntity> remoteFavorites = remoteFavoriteDataSource.getFavorites();
        // TODO Use method for putting the entire collection at once
        for (FavoriteEntity remoteFavorite : remoteFavorites) {
            localFavoriteDataSource.putFavorite(remoteFavorite);
        }
        return favoriteEntityMapper.transformEntities(remoteFavorites);
    }

    @Override
    public Favorite getFavoriteByEvent(String eventId) {
        FavoriteEntity favoriteEntity = remoteFavoriteDataSource.getFavoriteByIdEvent(eventId);
        return favoriteEntityMapper.transform(favoriteEntity);
    }

    @Override
    public void removeFavoriteByEvent(String eventId) {
        try {
            syncTrigger.triggerSync();
            remoteFavoriteDataSource.removeFavoriteByIdEvent(eventId);
            localFavoriteDataSource.removeFavoriteByIdEvent(eventId);
        } catch (ServerCommunicationException error) {
            queueUpload(buildDeletedEntity(eventId), error);
        }
    }

    private FavoriteEntity buildDeletedEntity(String eventId) {
        FavoriteEntity deletedEntity = new FavoriteEntity();
        deletedEntity.setIdEvent(eventId);
        deletedEntity.setSynchronizedStatus(LocalSynchronized.SYNC_DELETED);
        return deletedEntity;
    }

    private void markEntitySynchronized(FavoriteEntity favoriteEntity) {
        favoriteEntity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
    }

    private void queueUpload(FavoriteEntity favoriteEntity, ServerCommunicationException reason) {
        Timber.w(reason,
          "Favorite upload queued: idEvent %s; order %d",
          favoriteEntity.getIdEvent(),
          favoriteEntity.getOrder());
        prepareEntityForSynchronization(favoriteEntity);
        syncTrigger.notifyNeedsSync(this);
    }

    private void prepareEntityForSynchronization(FavoriteEntity favoriteEntity) {
        if (!isReadyForSync(favoriteEntity)) {
            favoriteEntity.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        }
        localFavoriteDataSource.putFavorite(favoriteEntity);
    }

    private boolean isReadyForSync(FavoriteEntity favoriteEntity) {
        return LocalSynchronized.SYNC_UPDATED.equals(favoriteEntity.getSynchronizedStatus())
          || LocalSynchronized.SYNC_NEW.equals(favoriteEntity.getSynchronizedStatus())
          || LocalSynchronized.SYNC_DELETED.equals(favoriteEntity.getSynchronizedStatus());
    }

    @Override
    public void dispatchSync() {
        List<FavoriteEntity> notSynchronized = localFavoriteDataSource.getEntitiesNotSynchronized();
        for (FavoriteEntity favoriteEntityEntity : notSynchronized) {
            if (LocalSynchronized.SYNC_DELETED.equals(favoriteEntityEntity.getSynchronizedStatus())) {
                remoteFavoriteDataSource.removeFavoriteByIdEvent(favoriteEntityEntity.getIdEvent());
                localFavoriteDataSource.removeFavoriteByIdEvent(favoriteEntityEntity.getIdEvent());
            } else {
                FavoriteEntity synchedEntity = remoteFavoriteDataSource.putFavorite(favoriteEntityEntity);
                synchedEntity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
                localFavoriteDataSource.putFavorite(synchedEntity);
            }
        }
    }
}
