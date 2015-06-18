package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.db.manager.FavoriteManager;
import java.util.ArrayList;
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
    public FavoriteEntity getFavoriteByIdEvent(String idEvent) {
        return favoriteManager.getFavoriteByIdEvent(idEvent);
    }

    @Override
    public List<FavoriteEntity> getFavorites() {
        return favoriteManager.getFavorites();
    }

    @Override
    public void removeFavoriteByIdEvent(String eventId) {
        favoriteManager.deleteEventByIdEvent(eventId);
    }

    @Override
    public List<FavoriteEntity> getEntitiesNotSynchronized() {
        return favoriteManager.getFavoritesNotSynchronized();
    }
}
