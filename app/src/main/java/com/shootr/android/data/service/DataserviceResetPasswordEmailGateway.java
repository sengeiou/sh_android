package com.shootr.android.data.service;

import com.shootr.android.domain.service.user.ResetPasswordEmailGateway;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceResetPasswordEmailGateway implements ResetPasswordEmailGateway {

    private final ShootrService shootrService;

    @Inject public DataserviceResetPasswordEmailGateway(ShootrService shootrService) {
        this.shootrService = shootrService;
    }

    @Override public void sendPasswordResetEmail(String idUser) throws IOException {
        shootrService.sendResetPasswordEmail(idUser);
    }
}
