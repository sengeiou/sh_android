package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.repository.datasource.SyncableDataSource;
import com.shootr.android.domain.exception.StreamAlreadyInFavoritesException;
import java.util.List;

public interface FavoriteDataSource extends SyncableDataSource<FavoriteEntity> {

    FavoriteEntity putFavorite(FavoriteEntity favoriteEntity) throws StreamAlreadyInFavoritesException;

    FavoriteEntity getFavoriteByIdStream(String idStream);

    List<FavoriteEntity> getFavorites(String userId);

    void removeFavoriteByIdStream(String streamId);

    void clear();
}
