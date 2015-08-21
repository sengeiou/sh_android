package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.service.StreamApiService;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.domain.exception.DeleteStreamNotAllowedException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
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
            return service.getStreamsByIds(streamIds);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public StreamEntity putStream(StreamEntity streamEntity) {
        try {
            return service.saveStream(streamEntity);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<StreamEntity> putStreams(List<StreamEntity> streams) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Integer getListingCount(String idUser) {
        try {
            return service.getListingCount(idUser);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<StreamEntity> getStreamsListing(String idUser) {
        try {
            return streamApiService.getStreamListing(idUser, MAX_NUMBER_OF_LISTING_STREAMS);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public void deleteStream(String idStream) throws DeleteStreamNotAllowedException {
        try {
            streamApiService.deleteStream(idStream);
        } catch (ApiException apiError) {
            if (apiError.getErrorInfo() == ErrorInfo.StreamHasWatchersException) {
                throw new DeleteStreamNotAllowedException();
            } else {
                throw new ServerCommunicationException(apiError);
            }
        } catch (IOException networkError) {
            throw new ServerCommunicationException(networkError);
        }
    }

    @Override public Integer getWatchersForStream(String idStream) {
        try {
            return streamApiService.getWatchersForStream(idStream);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
