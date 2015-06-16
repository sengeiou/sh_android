package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.entity.Synchronized;
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
        FavoriteEntity currentOrNewEntity = syncableFavoriteEntityFactory.currentOrNewEntity(favorite);
        try {
            FavoriteEntity remoteFavoriteEntity = remoteFavoriteDataSource.putFavorite(currentOrNewEntity);
            markEntitySynchronized(remoteFavoriteEntity);
            localFavoriteDataSource.putFavorite(remoteFavoriteEntity);
        } catch (ServerCommunicationException error) {
            queueUpload(currentOrNewEntity, error);
        }
    }

    @Override
    public List<Favorite> getFavorites() {
        return favoriteEntityMapper.transformEntities(remoteFavoriteDataSource.getFavorites());
    }

    @Override
    public Favorite getFavoriteByEvent(String eventId) {
        FavoriteEntity favoriteEntity = remoteFavoriteDataSource.getFavoriteByIdEvent(eventId);
        return favoriteEntityMapper.transform(favoriteEntity);
    }

    private void markEntitySynchronized(FavoriteEntity favoriteEntity) {
        favoriteEntity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
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
            favoriteEntity.setSynchronizedStatus(Synchronized.SYNC_UPDATED);
        }
        localFavoriteDataSource.putFavorite(favoriteEntity);
    }

    private boolean isReadyForSync(FavoriteEntity favoriteEntity) {
        return Synchronized.SYNC_UPDATED.equals(favoriteEntity.getSynchronizedStatus())
          || Synchronized.SYNC_NEW.equals(favoriteEntity.getSynchronizedStatus())
          || Synchronized.SYNC_DELETED.equals(favoriteEntity.getSynchronizedStatus());
    }

    @Override
    public void dispatchSync() {
        List<FavoriteEntity> notSynchronized = localFavoriteDataSource.getEntitiesNotSynchronized();
        for (FavoriteEntity favoriteEntityEntity : notSynchronized) {
            FavoriteEntity synchedEntity = remoteFavoriteDataSource.putFavorite(favoriteEntityEntity);
            synchedEntity.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
            localFavoriteDataSource.putFavorite(synchedEntity);
        }
    }
}
