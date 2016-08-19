package com.shootr.mobile.data.repository.datasource.favorite;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;

public interface FavoriteDataSource extends SyncableDataSource<FavoriteEntity> {

    FavoriteEntity putFavorite(FavoriteEntity favoriteEntity) throws StreamAlreadyInFavoritesException;

    void removeFavoriteByIdStream(String streamId);
}
