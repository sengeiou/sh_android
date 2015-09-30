package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.entity.WatchersApiEntity;
import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.StreamApiService;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class ServiceStreamDataSource implements StreamDataSource {

    public static final int MAX_NUMBER_OF_LISTING_STREAMS = 100;
    private final ShootrService service;
    private final StreamApiService streamApiService;

    @Inject public ServiceStreamDataSource(ShootrService service, StreamApiService streamApiService) {
        this.service = service;
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

    @Override public Integer getListingCount(String idUser) {
        try {
            return streamApiService.getListingCount(idUser);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
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

    @Override public Map<String, Integer> getHolderWatchers() {
        try {
            Map<String, Integer> watchers = new HashMap<>();
            for (WatchersApiEntity watchersApiEntity : streamApiService.getHolderWatchers()) {
                watchers.put(watchersApiEntity.getIdStream(), watchersApiEntity.getWatchers());
            }
            return watchers;
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
