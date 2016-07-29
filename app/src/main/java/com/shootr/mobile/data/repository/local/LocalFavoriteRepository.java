package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.mapper.FavoriteEntityMapper;
import com.shootr.mobile.data.repository.datasource.event.FavoriteDataSource;
import com.shootr.mobile.data.repository.sync.SyncableFavoriteEntityFactory;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import com.shootr.mobile.domain.repository.FavoriteRepository;
import com.shootr.mobile.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;

public class LocalFavoriteRepository implements FavoriteRepository {

    private final FavoriteDataSource localFavoriteDataSource;
    private final FavoriteEntityMapper favoriteEntityMapper;
    private final SyncableFavoriteEntityFactory syncableFavoriteEntityFactory;

    @Inject public LocalFavoriteRepository(@Local FavoriteDataSource localFavoriteDataSource,
      FavoriteEntityMapper favoriteEntityMapper, SyncableFavoriteEntityFactory syncableFavoriteEntityFactory) {
        this.localFavoriteDataSource = localFavoriteDataSource;
        this.favoriteEntityMapper = favoriteEntityMapper;
        this.syncableFavoriteEntityFactory = syncableFavoriteEntityFactory;
    }

    @Override public void putFavorite(Favorite favorite) throws StreamAlreadyInFavoritesException {
        FavoriteEntity currentOrNewEntity = syncableFavoriteEntityFactory.updatedOrNewEntity(favorite);
        localFavoriteDataSource.putFavorite(currentOrNewEntity);
    }

    @Override public List<Favorite> getFavorites(String userId) {
        List<FavoriteEntity> favoriteEntities = localFavoriteDataSource.getFavorites(userId);
        return favoriteEntityMapper.transformEntities(favoriteEntities);
    }

    @Override public Favorite getFavoriteByStream(String eventId) {
        FavoriteEntity favoriteByIdEvent = localFavoriteDataSource.getFavoriteByIdStream(eventId);
        return favoriteEntityMapper.transform(favoriteByIdEvent);
    }

    @Override public void removeFavoriteByStream(String eventId) {
        localFavoriteDataSource.removeFavoriteByIdStream(eventId);
    }
}
