package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.service.EventApiService;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceStreamDataSource implements StreamDataSource {

    public static final int MAX_NUMBER_OF_LISTING_EVENTS = 100;
    private final ShootrService service;
    private final EventApiService eventService;

    @Inject public ServiceStreamDataSource(ShootrService service, EventApiService eventService) {
        this.service = service;
        this.eventService = eventService;
    }

    @Override public StreamEntity getStreamById(String idStream) {
        try {
            return service.getStreamById(idStream);
        } catch (IOException e) {
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
            return eventService.getEventListing(idUser, MAX_NUMBER_OF_LISTING_EVENTS);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public void deleteStream(String idStream) throws DeleteEventNotAllowedException {
        try {
            eventService.deleteEvent(idStream);
        } catch (ApiException apiError) {
            if (apiError.getErrorInfo() == ErrorInfo.EventHasWatchersException) {
                throw new DeleteEventNotAllowedException();
            } else {
                throw new ServerCommunicationException(apiError);
            }
        } catch (IOException networkError) {
            throw new ServerCommunicationException(networkError);
        }
    }
}
