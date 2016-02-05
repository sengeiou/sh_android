package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.api.entity.FavoritesApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.StreamApiService;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class ServiceStreamDataSource implements StreamDataSource {

    public static final int MAX_NUMBER_OF_LISTING_STREAMS = 100;
    private final StreamApiService streamApiService;

    @Inject public ServiceStreamDataSource(StreamApiService streamApiService) {
        this.streamApiService = streamApiService;
    }

    @Override public StreamEntity getStreamById(String idStream) {
        try {
            return streamApiService.getStream(idStream);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<StreamEntity> getStreamByIds(List<String> streamIds) {
        try {
            return streamApiService.getStreams(streamIds);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public StreamEntity putStream(StreamEntity streamEntity) {
        try {
            if (streamEntity.getIdStream() == null) {
                return streamApiService.createStream(streamEntity);
            } else {
                return streamApiService.updateStream(streamEntity);
            }
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<StreamEntity> putStreams(List<StreamEntity> streams) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public List<StreamEntity> getStreamsListing(String idUser) {
        try {
            return streamApiService.getStreamListing(idUser, MAX_NUMBER_OF_LISTING_STREAMS);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void shareStream(String idStream) {
        try {
            streamApiService.shareStream(idStream);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public Map<String, Integer> getHolderFavorites(String idUser) {
        try {
            Map<String, Integer> favorites = new HashMap<>();
            List<FavoritesApiEntity> holderFavorites = streamApiService.getHolderFavorites(idUser);
            for (FavoritesApiEntity favoritesApiEntity : holderFavorites) {
                favorites.put(favoritesApiEntity.getIdStream(), favoritesApiEntity.getFavoriteCount());
            }
            return favorites;
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void removeStream(String idStream) {
        try {
            streamApiService.removeStream(idStream);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void restoreStream(String idStream) {
        try {
            streamApiService.restoreStream(idStream);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public StreamEntity getBlogStream(String country, String language) {
        try {
            return streamApiService.getBlogStream(country, language);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public StreamEntity getHelpStream(String country, String locale) {
        try {
            return streamApiService.getHelpStream(country, locale);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
