package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.StreamApiService;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceStreamListDataSource implements StreamListDataSource {

    private final StreamApiService streamApiService;

    @Inject public ServiceStreamListDataSource(StreamApiService streamApiService) {
        this.streamApiService = streamApiService;
    }

    @Override public List<StreamEntity> getStreamList(String locale, String[] types) {
        try {
            return streamApiService.getStreamList(locale, types);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<StreamEntity> getStreams(String query, String locale, String[] types) {
        try {
            return streamApiService.getStreams(query, locale, types);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
