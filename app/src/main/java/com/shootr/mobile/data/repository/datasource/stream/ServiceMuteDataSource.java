package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.StreamApiService;
import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceMuteDataSource implements MuteDataSource {

    public static final String METHOD_NOT_VALID_FOR_SERVICE = "Method not valid for service";
    private final StreamApiService streamApiService;

    @Inject public ServiceMuteDataSource(StreamApiService streamApiService) {
        this.streamApiService = streamApiService;
    }

    @Override public void mute(MuteStreamEntity muteStreamEntity) {
        try {
            streamApiService.mute(muteStreamEntity.getIdStream());
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void unmute(String idStream) {
        try {
            streamApiService.unmute(idStream);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<MuteStreamEntity> getMutedStreamEntities() {
        try {
            return streamApiService.getMuteStreams();
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public List<MuteStreamEntity> getEntitiesNotSynchronized() {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
    }

    @Override public void putMuteds(List<MuteStreamEntity> muteStreamEntities) {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
    }

    @Override public MuteStreamEntity getMute(String idStream) {
        throw new IllegalStateException(METHOD_NOT_VALID_FOR_SERVICE);
    }
}
