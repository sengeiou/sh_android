package com.shootr.mobile.domain.repository.favorite;

import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;

public interface FavoriteRepository {

    void putFavorite(Favorite favorite) throws StreamAlreadyInFavoritesException;

    void removeFavoriteByStream(String idStream);
}
