package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import java.util.List;

public interface FavoriteDataSource extends SyncableDataSource<FavoriteEntity> {

    FavoriteEntity putFavorite(FavoriteEntity favoriteEntity) throws StreamAlreadyInFavoritesException;

    FavoriteEntity getFavoriteByIdStream(String idStream);

    List<FavoriteEntity> getFavorites(String userId);

    void removeFavoriteByIdStream(String streamId);

    void clear();
}
