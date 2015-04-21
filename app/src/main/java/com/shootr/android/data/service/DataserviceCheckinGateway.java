package com.shootr.android.data.service;

import com.shootr.android.domain.service.user.CheckinGateway;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceCheckinGateway implements CheckinGateway {

    private final ShootrService shootrService;

    @Inject public DataserviceCheckinGateway(ShootrService shootrService) {
        this.shootrService = shootrService;
    }

    @Override public void performCheckin(Long idUser, Long idEvent) throws IOException {
        shootrService.performCheckin(idUser, idEvent);
    }
}