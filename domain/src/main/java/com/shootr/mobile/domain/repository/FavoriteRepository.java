package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Favorite;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;

import java.util.List;

public interface FavoriteRepository {

    void putFavorite(Favorite favorite) throws StreamAlreadyInFavoritesException;

    List<Favorite> getFavorites(String userId);

    Favorite getFavoriteByStream(String eventId);

    void removeFavoriteByStream(String eventId);
}
