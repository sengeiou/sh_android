package com.shootr.mobile.data.repository.datasource.favorite;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.db.manager.FavoriteManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseFavoriteDatasource implements InternalFavoriteDatasource {

    private final FavoriteManager favoriteManager;

    @Inject public DatabaseFavoriteDatasource(FavoriteManager favoriteManager) {
        this.favoriteManager = favoriteManager;
    }

    @Override public FavoriteEntity putFavorite(FavoriteEntity favoriteEntity) {
        return favoriteManager.saveFavorite(favoriteEntity);
    }

    @Override public FavoriteEntity getFavoriteByIdStream(String idStream) {
        return favoriteManager.getFavoriteByIdStream(idStream);
    }

    @Override public List<FavoriteEntity> getFavorites() {
        return favoriteManager.getFavorites();
    }

    @Override public void removeFavoriteByIdStream(String streamId) {
        favoriteManager.deleteStreamByIdStream(streamId);
    }

    @Override public void clear() {
        favoriteManager.deleteAll();
    }

    @Override public List<FavoriteEntity> getEntitiesNotSynchronized() {
        return favoriteManager.getFavoritesNotSynchronized();
    }
}
