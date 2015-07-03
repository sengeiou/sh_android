package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.mapper.FavoriteEntityMapper;
import com.shootr.android.data.repository.datasource.event.FavoriteDataSource;
import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.repository.Local;
import javax.inject.Inject;

public class SyncableFavoriteEntityFactory extends SyncableEntityFactory<Favorite, FavoriteEntity> {

    private final FavoriteDataSource localFavoriteDataSource;
    private final FavoriteEntityMapper favoriteEntityMapper;

    @Inject public SyncableFavoriteEntityFactory(@Local FavoriteDataSource localFavoriteDataSource,
      FavoriteEntityMapper favoriteEntityMapper) {
        this.localFavoriteDataSource = localFavoriteDataSource;
        this.favoriteEntityMapper = favoriteEntityMapper;
    }

    @Override
    protected FavoriteEntity currentEntity(Favorite favorite) {
        return localFavoriteDataSource.getFavoriteByIdEvent(favorite.getIdEvent());
    }

    @Override
    protected FavoriteEntity updateValues(FavoriteEntity currentFavoriteEntity, Favorite favorite) {
        FavoriteEntity favoriteEntityFromDomain = favoriteEntityMapper.transform(favorite);
        favoriteEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_UPDATED);
        return favoriteEntityFromDomain;
    }

    @Override
    protected FavoriteEntity createNewEntity(Favorite favorite) {
        FavoriteEntity newEntityFromDomain = favoriteEntityMapper.transform(favorite);
        newEntityFromDomain.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        return newEntityFromDomain;
    }
}
