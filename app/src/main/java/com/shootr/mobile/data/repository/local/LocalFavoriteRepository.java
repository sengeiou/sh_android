package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.mapper.FavoriteEntityMapper;
import com.shootr.mobile.data.repository.datasource.favorite.InternalFavoriteDatasource;
import com.shootr.mobile.data.repository.sync.SyncableFavoriteEntityFactory;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalFavoriteRepository implements InternalFavoriteRepository {

    private final InternalFavoriteDatasource localFavoriteDataSource;
    private final FavoriteEntityMapper favoriteEntityMapper;
    private final SyncableFavoriteEntityFactory syncableFavoriteEntityFactory;

    @Inject public LocalFavoriteRepository(InternalFavoriteDatasource localFavoriteDataSource,
      FavoriteEntityMapper favoriteEntityMapper, SyncableFavoriteEntityFactory syncableFavoriteEntityFactory) {
        this.localFavoriteDataSource = localFavoriteDataSource;
        this.favoriteEntityMapper = favoriteEntityMapper;
        this.syncableFavoriteEntityFactory = syncableFavoriteEntityFactory;
    }

    @Override public void putFavorite(Favorite favorite) throws StreamAlreadyInFavoritesException {
        FavoriteEntity currentOrNewEntity = syncableFavoriteEntityFactory.updatedOrNewEntity(favorite);
        localFavoriteDataSource.putFavorite(currentOrNewEntity);
    }

    @Override public List<Favorite> getFavorites() {
        List<FavoriteEntity> favoriteEntities = localFavoriteDataSource.getFavorites();
        return favoriteEntityMapper.transformEntities(favoriteEntities);
    }

    @Override public Favorite getFavoriteByStream(String idStream) {
        FavoriteEntity favoriteByIdEvent = localFavoriteDataSource.getFavoriteByIdStream(idStream);
        return favoriteEntityMapper.transform(favoriteByIdEvent);
    }

    @Override public void removeFavoriteByStream(String idStream) {
        localFavoriteDataSource.removeFavoriteByIdStream(idStream);
    }
}
