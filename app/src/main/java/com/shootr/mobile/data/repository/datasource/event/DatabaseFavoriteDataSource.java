package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.db.manager.FavoriteManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseFavoriteDataSource implements FavoriteDataSource {

    private final FavoriteManager favoriteManager;

    @Inject
    public DatabaseFavoriteDataSource(FavoriteManager favoriteManager) {
        this.favoriteManager = favoriteManager;
    }

    @Override
    public FavoriteEntity putFavorite(FavoriteEntity favoriteEntity) {
        return favoriteManager.saveFavorite(favoriteEntity);
    }

    @Override
    public FavoriteEntity getFavoriteByIdStream(String idStream) {
        return favoriteManager.getFavoriteByIdStream(idStream);
    }

    @Override
    public List<FavoriteEntity> getFavorites(String userId) {
        return favoriteManager.getFavorites();
    }

    @Override
    public void removeFavoriteByIdStream(String streamId) {
        favoriteManager.deleteStreamByIdStream(streamId);
    }

    @Override
    public void clear() {
        favoriteManager.deleteAll();
    }

    @Override
    public List<FavoriteEntity> getEntitiesNotSynchronized() {
        return favoriteManager.getFavoritesNotSynchronized();
    }
}
