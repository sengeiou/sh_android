package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.entity.FavoriteApiEntity;
import com.shootr.android.data.api.entity.mapper.FavoriteApiEntityMapper;
import com.shootr.android.data.api.service.FavoriteApiService;
import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class ServiceFavoriteDataSource implements FavoriteDataSource {

    private final FavoriteApiService favoriteApiService;
    private final SessionRepository sessionRepository;
    private final FavoriteApiEntityMapper favoriteApiEntityMapper;

    @Inject
    public ServiceFavoriteDataSource(FavoriteApiService favoriteApiService,
      SessionRepository sessionRepository,
      FavoriteApiEntityMapper favoriteApiEntityMapper) {
        this.favoriteApiService = favoriteApiService;
        this.sessionRepository = sessionRepository;
        this.favoriteApiEntityMapper = favoriteApiEntityMapper;
    }

    @Override
    public FavoriteEntity putFavorite(FavoriteEntity favoriteEntity) {
        FavoriteApiEntity favoriteFromApi = favoriteApiService.createFavorite(currentUserId(), favoriteEntity);
        return favoriteApiEntityMapper.transform(favoriteFromApi);
    }

    @Override
    public FavoriteEntity getFavoriteByIdEvent(String idEvent) {
        throw new IllegalStateException("Method not implemented id service datasource");
    }

    @Override
    public List<FavoriteEntity> getFavorites() {
        List<FavoriteApiEntity> favorites = favoriteApiService.getFavorites(currentUserId());
        return favoriteApiEntityMapper.transform(favorites);
    }

    @Override
    public List<FavoriteEntity> getEntitiesNotSynchronized() {
        throw new IllegalStateException("Method not available in Service");
    }

    private String currentUserId() {
        return sessionRepository.getCurrentUserId();
    }
}
