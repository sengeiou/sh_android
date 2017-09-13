package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.mapper.FavoriteEntityMapper;
import com.shootr.mobile.data.mapper.OnBoardingStreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.favorite.ExternalFavoriteDatasource;
import com.shootr.mobile.data.repository.datasource.favorite.InternalFavoriteDatasource;
import com.shootr.mobile.data.repository.sync.SyncTrigger;
import com.shootr.mobile.data.repository.sync.SyncableFavoriteEntityFactory;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.OnBoarding;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class SyncFavoriteRepository implements ExternalFavoriteRepository, SyncableRepository {

  private final ExternalFavoriteDatasource remoteFavoriteDataSource;
  private final InternalFavoriteDatasource localFavoriteDataSource;
  private final FavoriteEntityMapper favoriteEntityMapper;
  private final SyncableFavoriteEntityFactory syncableFavoriteEntityFactory;
  private final OnBoardingStreamEntityMapper onBoardingStreamEntityMapper;
  private final SyncTrigger syncTrigger;
  private final SessionRepository sessionRepository;

  @Inject public SyncFavoriteRepository(ExternalFavoriteDatasource remoteFavoriteDataSource,
      InternalFavoriteDatasource localFavoriteDataSource, FavoriteEntityMapper favoriteEntityMapper,
      SyncableFavoriteEntityFactory syncableFavoriteEntityFactory,
      OnBoardingStreamEntityMapper suggestedStreamEntityMapper, SyncTrigger syncTrigger,
      SessionRepository sessionRepository) {
    this.remoteFavoriteDataSource = remoteFavoriteDataSource;
    this.localFavoriteDataSource = localFavoriteDataSource;
    this.favoriteEntityMapper = favoriteEntityMapper;
    this.syncableFavoriteEntityFactory = syncableFavoriteEntityFactory;
    this.onBoardingStreamEntityMapper = suggestedStreamEntityMapper;
    this.syncTrigger = syncTrigger;
    this.sessionRepository = sessionRepository;
  }

  @Override public void putFavorite(Favorite favorite) throws StreamAlreadyInFavoritesException {
    FavoriteEntity updatedOrNewEntity = syncableFavoriteEntityFactory.updatedOrNewEntity(favorite);
    try {
      FavoriteEntity remoteFavoriteEntity =
          remoteFavoriteDataSource.putFavorite(updatedOrNewEntity);
      markEntitySynchronized(remoteFavoriteEntity);
      localFavoriteDataSource.putFavorite(remoteFavoriteEntity);
      syncTrigger.triggerSync();
    } catch (ServerCommunicationException error) {
      queueUpload(updatedOrNewEntity, error);
    }
  }

  @Override public List<Favorite> getFavorites(String userId) {
    syncTrigger.triggerSync();
    List<FavoriteEntity> remoteFavorites = remoteFavoriteDataSource.getFavorites(userId);
    if (userId.equals(sessionRepository.getCurrentUserId())) {
      localFavoriteDataSource.clear();
      // TODO Use method for putting the entire collection at once
      for (FavoriteEntity remoteFavorite : remoteFavorites) {
        try {
          localFavoriteDataSource.putFavorite(remoteFavorite);
        } catch (StreamAlreadyInFavoritesException e) {
                    /* swallow it */
        }
      }
    }
    return favoriteEntityMapper.transformEntities(remoteFavorites);
  }

  @Override public List<OnBoarding> getOnBoardingStreams(String type, String locale) {
    return onBoardingStreamEntityMapper.map(remoteFavoriteDataSource.getOnBoardingStreams(type, locale));
  }

  @Override public void addSuggestedFavorites(List<String> idStreams) {
    remoteFavoriteDataSource.addFavorites(idStreams);
  }

  @Override public void removeFavoriteByStream(String idStream) {
    try {
      syncTrigger.triggerSync();
      remoteFavoriteDataSource.removeFavoriteByIdStream(idStream);
      localFavoriteDataSource.removeFavoriteByIdStream(idStream);
    } catch (ServerCommunicationException error) {
      queueUpload(buildDeletedEntity(idStream), error);
    }
  }

  private FavoriteEntity buildDeletedEntity(String streamId) {
    FavoriteEntity deletedEntity = new FavoriteEntity();
    deletedEntity.setIdStream(streamId);
    deletedEntity.setSynchronizedStatus(LocalSynchronized.SYNC_DELETED);
    return deletedEntity;
  }

  private void markEntitySynchronized(FavoriteEntity favoriteEntity) {
    favoriteEntity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
  }

  private void queueUpload(FavoriteEntity favoriteEntity, ServerCommunicationException reason) {
    Timber.w(reason, "Favorite upload queued: idEvent %s; order %d", favoriteEntity.getIdStream(),
        favoriteEntity.getOrder());
    prepareEntityForSynchronization(favoriteEntity);
    syncTrigger.notifyNeedsSync(this);
  }

  private void prepareEntityForSynchronization(FavoriteEntity favoriteEntity) {
    if (!isReadyForSync(favoriteEntity)) {
      favoriteEntity.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
    }
    try {
      localFavoriteDataSource.putFavorite(favoriteEntity);
    } catch (StreamAlreadyInFavoritesException e) {
            /* swallow it */
    }
  }

  private boolean isReadyForSync(FavoriteEntity favoriteEntity) {
    return LocalSynchronized.SYNC_UPDATED.equals(favoriteEntity.getSynchronizedStatus())
        || LocalSynchronized.SYNC_NEW.equals(favoriteEntity.getSynchronizedStatus())
        || LocalSynchronized.SYNC_DELETED.equals(favoriteEntity.getSynchronizedStatus());
  }

  @Override public void dispatchSync() {
    List<FavoriteEntity> notSynchronized = localFavoriteDataSource.getEntitiesNotSynchronized();
    for (FavoriteEntity favoriteEntityEntity : notSynchronized) {
      if (LocalSynchronized.SYNC_DELETED.equals(favoriteEntityEntity.getSynchronizedStatus())) {
        remoteFavoriteDataSource.removeFavoriteByIdStream(favoriteEntityEntity.getIdStream());
        localFavoriteDataSource.removeFavoriteByIdStream(favoriteEntityEntity.getIdStream());
      } else {
        try {
          FavoriteEntity favoriteEntity =
              remoteFavoriteDataSource.putFavorite(favoriteEntityEntity);
          favoriteEntity.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
          localFavoriteDataSource.putFavorite(favoriteEntity);
        } catch (StreamAlreadyInFavoritesException e) {
                    /* swallow it */
        }
      }
    }
  }
}
