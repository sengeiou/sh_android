package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.api.service.StreamApiService;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceStreamListDataSource implements StreamListDataSource {

    private final StreamApiService streamApiService;

    @Inject public ServiceStreamListDataSource(StreamApiService streamApiService) {
        this.streamApiService = streamApiService;
    }

    @Override
    public List<StreamEntity> getStreamList(String locale) {
        try {
            return streamApiService.getStreamList(locale);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<StreamEntity> getStreams(String query, String locale) {
        try {
            return streamApiService.getStreams(query, locale);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
