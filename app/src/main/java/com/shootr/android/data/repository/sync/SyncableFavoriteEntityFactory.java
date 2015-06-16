package com.shootr.android.data.repository.sync;

import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.mapper.FavoriteEntityMapper;
import com.shootr.android.data.repository.datasource.event.FavoriteDataSource;
import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.repository.Local;
import java.util.Date;
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
        favoriteEntityFromDomain.setBirth(currentFavoriteEntity.getBirth());
        favoriteEntityFromDomain.setModified(new Date());
        favoriteEntityFromDomain.setRevision(currentFavoriteEntity.getRevision() + 1);
        favoriteEntityFromDomain.setSynchronizedStatus(Synchronized.SYNC_UPDATED);
        favoriteEntityFromDomain.setDeleted(currentFavoriteEntity.getDeleted());
        return favoriteEntityFromDomain;
    }

    @Override
    protected FavoriteEntity createNewEntity(Favorite favorite) {
        FavoriteEntity newEntityFromDomain = favoriteEntityMapper.transform(favorite);
        newEntityFromDomain.setSynchronizedStatus(Synchronized.SYNC_NEW);
        Date now = new Date();
        newEntityFromDomain.setBirth(now);
        newEntityFromDomain.setModified(now);
        newEntityFromDomain.setRevision(0);
        return newEntityFromDomain;
    }
}
