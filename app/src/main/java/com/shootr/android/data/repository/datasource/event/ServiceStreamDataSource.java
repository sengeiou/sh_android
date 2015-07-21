package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.exception.ErrorInfo;
import com.shootr.android.data.api.service.EventApiService;
import com.shootr.android.data.entity.EventEntity;
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

    @Override public EventEntity getStreamById(String idStream) {
        try {
            return service.getEventById(idStream);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<EventEntity> getStreamByIds(List<String> streamIds) {
        try {
            return service.getEventsByIds(streamIds);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public EventEntity putStream(EventEntity streamEntity) {
        try {
            return service.saveEvent(streamEntity);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<EventEntity> putStreams(List<EventEntity> streams) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Integer getListingCount(String idUser) {
        try {
            return service.getListingCount(idUser);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<EventEntity> getStreamsListing(String idUser) {
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
