package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.api.SocketApi;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ShotApiService;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.repository.nice.NiceShotRepository;
import java.io.IOException;
import javax.inject.Inject;

public class RemoteNiceShotRepository implements NiceShotRepository {

    private final ShotApiService shotApiService;
    private final BusPublisher busPublisher;
    private final SocketApi socketApi;

    @Inject public RemoteNiceShotRepository(ShotApiService shotApiService,
        BusPublisher busPublisher, SocketApi socketApi) {
        this.shotApiService = shotApiService;
        this.busPublisher = busPublisher;
        this.socketApi = socketApi;
    }

    @Override public void mark(String idShot) {
        try {
            //TODO poner if para saber desde donde enviar el nice
            //socketApi.sendNice(idShot);
            shotApiService.markNice(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public void unmark(String idShot) {
        try {
            shotApiService.unmarkNice(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
