package com.shootr.android.domain.repository;

import com.shootr.android.domain.Favorite;
import java.util.List;

public interface FavoriteRepository {

    void putFavorite(Favorite favorite);

    List<Favorite> getFavorites();

    Favorite getFavoriteByStream(String eventId);

    void removeFavoriteByStream(String eventId);
}
