package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.mapper.FavoriteEntityMapper;
import com.shootr.android.data.repository.datasource.event.FavoriteDataSource;
import com.shootr.android.data.repository.sync.SyncableFavoriteEntityFactory;
import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
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

    @Override
    public void putFavorite(Favorite favorite) {
        FavoriteEntity currentOrNewEntity = syncableFavoriteEntityFactory.updatedOrNewEntity(favorite);
        localFavoriteDataSource.putFavorite(currentOrNewEntity);
    }

    @Override
    public List<Favorite> getFavorites() {
        List<FavoriteEntity> favoriteEntities = localFavoriteDataSource.getFavorites();
        return favoriteEntityMapper.transformEntities(favoriteEntities);
    }

    @Override
    public Favorite getFavoriteByStream(String eventId) {
        FavoriteEntity favoriteByIdEvent = localFavoriteDataSource.getFavoriteByIdEvent(eventId);
        return favoriteEntityMapper.transform(favoriteByIdEvent);
    }

    @Override
    public void removeFavoriteByStream(String eventId) {
        localFavoriteDataSource.removeFavoriteByIdEvent(eventId);
    }
}
