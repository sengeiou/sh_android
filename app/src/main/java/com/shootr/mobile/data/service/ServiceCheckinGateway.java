package com.shootr.mobile.data.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.StreamApiService;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.service.user.CheckinGateway;
import java.io.IOException;
import javax.inject.Inject;

public class ServiceCheckinGateway implements CheckinGateway {

    private final StreamApiService streamApiService;

    @Inject public ServiceCheckinGateway(StreamApiService streamApiService) {
        this.streamApiService = streamApiService;
    }

    @Override public void performCheckin(String idEvent){
        try {
            streamApiService.checkIn(idEvent);
        } catch (IOException | ApiException e) {
            throw new ServerCommunicationException(e);
        }
    }
}
