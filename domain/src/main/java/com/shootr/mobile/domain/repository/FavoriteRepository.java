package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import java.util.List;

public interface FavoriteRepository {

    void putFavorite(com.shootr.mobile.domain.Favorite favorite) throws StreamAlreadyInFavoritesException;

    List<com.shootr.mobile.domain.Favorite> getFavorites(String userId);

    com.shootr.mobile.domain.Favorite getFavoriteByStream(String eventId);

    void removeFavoriteByStream(String eventId);
}
