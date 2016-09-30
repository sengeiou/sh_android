package com.shootr.mobile.data.repository.datasource.favorite;

import com.shootr.mobile.data.api.entity.FavoriteApiEntity;
import com.shootr.mobile.data.api.entity.mapper.FavoriteApiEntityMapper;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.FavoriteApiService;
import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import com.shootr.mobile.domain.repository.Local;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ServiceFavoriteDataSource implements ExternalFavoriteDatasource {

    private final FavoriteApiService favoriteApiService;
    private final FavoriteApiEntityMapper favoriteApiEntityMapper;
    private final StreamDataSource localStreamDataSource;

    @Inject public ServiceFavoriteDataSource(FavoriteApiService favoriteApiService,
      FavoriteApiEntityMapper favoriteApiEntityMapper, @Local StreamDataSource localStreamDataSource) {
        this.favoriteApiService = favoriteApiService;
        this.favoriteApiEntityMapper = favoriteApiEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
    }

    @Override public FavoriteEntity putFavorite(FavoriteEntity favoriteEntity)
      throws StreamAlreadyInFavoritesException {
        try {
            FavoriteApiEntity favoriteFromApi = favoriteApiService.createFavorite(favoriteEntity);
            return favoriteApiEntityMapper.transform(favoriteFromApi);
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        } catch (ApiException e) {
            throw new StreamAlreadyInFavoritesException(e);
        }
    }

    @Override public List<FavoriteEntity> getFavorites(String userId) {
        try {
            List<FavoriteApiEntity> favorites = favoriteApiService.getFavorites(userId);
            favorites = filterFavoritesWithoutStreams(favorites);
            storeEmbedStreams(favorites);
            return favoriteApiEntityMapper.transform(favorites);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    private List<FavoriteApiEntity> filterFavoritesWithoutStreams(List<FavoriteApiEntity> favorites) {
        List<FavoriteApiEntity> filtered = new ArrayList<>(favorites.size());
        for (FavoriteApiEntity favorite : favorites) {
            if (favorite.getStream() != null) {
                filtered.add(favorite);
            }
        }
        return filtered;
    }

    @Override public void removeFavoriteByIdStream(String streamId) {
        try {
            favoriteApiService.deleteFavorite(streamId);
        } catch (ApiException | IOException error) {
            throw new ServerCommunicationException(error);
        }
    }

    @Override public List<FavoriteEntity> getEntitiesNotSynchronized() {
        throw new IllegalStateException("Method not available in Service");
    }

    private void storeEmbedStreams(List<FavoriteApiEntity> favorites) {
        for (FavoriteApiEntity favorite : favorites) {
            StreamEntity stream = favorite.getStream();
            localStreamDataSource.putStream(stream);
        }
    }
}
