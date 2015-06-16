package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.repository.datasource.SyncableDataSource;
import java.util.List;

public interface FavoriteDataSource extends SyncableDataSource<FavoriteEntity> {

    FavoriteEntity putFavorite(FavoriteEntity favoriteEntity);

    FavoriteEntity getFavoriteByIdEvent(String idEvent);

    List<FavoriteEntity> getFavorites();
}
