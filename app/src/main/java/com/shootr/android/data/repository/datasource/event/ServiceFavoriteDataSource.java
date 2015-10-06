package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.entity.FavoriteApiEntity;
import com.shootr.android.data.api.entity.mapper.FavoriteApiEntityMapper;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.FavoriteApiService;
import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.StreamAlreadyInFavoritesException;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceFavoriteDataSource implements FavoriteDataSource {

    private final FavoriteApiService favoriteApiService;
    private final SessionRepository sessionRepository;
    private final FavoriteApiEntityMapper favoriteApiEntityMapper;
    private final StreamDataSource localStreamDataSource;

    @Inject
    public ServiceFavoriteDataSource(FavoriteApiService favoriteApiService,
      SessionRepository sessionRepository,
      FavoriteApiEntityMapper favoriteApiEntityMapper,
      @Local StreamDataSource localStreamDataSource) {
        this.favoriteApiService = favoriteApiService;
        this.sessionRepository = sessionRepository;
        this.favoriteApiEntityMapper = favoriteApiEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
    }

    @Override
    public FavoriteEntity putFavorite(FavoriteEntity favoriteEntity) throws StreamAlreadyInFavoritesException {
        try {
            FavoriteApiEntity favoriteFromApi = favoriteApiService.createFavorite(favoriteEntity);
            return favoriteApiEntityMapper.transform(favoriteFromApi);
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        } catch (ApiException e) {
            throw new StreamAlreadyInFavoritesException(e);
        }
    }

    @Override
    public FavoriteEntity getFavoriteByIdStream(String idStream) {
        throw new IllegalStateException("Method not implemented in service datasource");
    }

    @Override
    public List<FavoriteEntity> getFavorites() {
        try {
            List<FavoriteApiEntity> favorites = favoriteApiService.getFavorites();
            storeEmbedStreams(favorites);
            return favoriteApiEntityMapper.transform(favorites);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }

    }

    @Override
    public void removeFavoriteByIdStream(String streamId) {
        try {
            favoriteApiService.deleteFavorite(streamId);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override
    public void clear() {
        throw new IllegalStateException("Method not available in Service");
    }

    @Override
    public List<FavoriteEntity> getEntitiesNotSynchronized() {
        throw new IllegalStateException("Method not available in Service");
    }

    private void storeEmbedStreams(List<FavoriteApiEntity> favorites) {
        for (FavoriteApiEntity favorite : favorites) {
            StreamEntity stream = favorite.getStream();
            localStreamDataSource.putStream(stream);
        }
    }

    private String currentUserId() {
        return sessionRepository.getCurrentUserId();
    }
}
