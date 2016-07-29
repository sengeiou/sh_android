package com.shootr.mobile.data.repository.sync;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.mapper.FavoriteEntityMapper;
import com.shootr.mobile.data.repository.datasource.event.FavoriteDataSource;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.repository.Local;
import javax.inject.Inject;

public class SyncableFavoriteEntityFactory extends SyncableEntityFactory<Favorite, FavoriteEntity> {

    private final FavoriteDataSource localFavoriteDataSource;
    private final FavoriteEntityMapper favoriteEntityMapper;

    @Inject public SyncableFavoriteEntityFactory(@Local FavoriteDataSource localFavoriteDataSource,
      FavoriteEntityMapper favoriteEntityMapper) {
        this.localFavoriteDataSource = localFavoriteDataSource;
        this.favoriteEntityMapper = favoriteEntityMapper;
    }

    @Override protected FavoriteEntity currentEntity(Favorite favorite) {
        return localFavoriteDataSource.getFavoriteByIdStream(favorite.getIdStream());
    }

    @Override protected FavoriteEntity updateValues(FavoriteEntity currentFavoriteEntity, Favorite favorite) {
        FavoriteEntity favoriteEntityFromDomain = favoriteEntityMapper.transform(favorite);
        favoriteEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        return favoriteEntityFromDomain;
    }

    @Override protected FavoriteEntity createNewEntity(Favorite favorite) {
        FavoriteEntity newEntityFromDomain = favoriteEntityMapper.transform(favorite);
        newEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        return newEntityFromDomain;
    }
}
