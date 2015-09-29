package com.shootr.android.data.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.StreamApiService;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.service.user.CheckinGateway;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceCheckinGateway implements CheckinGateway {

    private final StreamApiService streamApiService;

    @Inject public DataserviceCheckinGateway(StreamApiService streamApiService) {
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
