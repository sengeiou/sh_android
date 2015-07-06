package com.shootr.android.data.service;

import com.shootr.android.data.api.service.ResetPasswordApiService;
import com.shootr.android.data.entity.ResetEntity;
import com.shootr.android.domain.service.user.ResetPasswordEmailGateway;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceResetPasswordEmailGateway implements ResetPasswordEmailGateway {

    private final ResetPasswordApiService resetPasswordApiService;

    @Inject public DataserviceResetPasswordEmailGateway(ResetPasswordApiService resetPasswordApiService) {
        this.resetPasswordApiService = resetPasswordApiService;
    }

    @Override public void sendPasswordResetEmail(String idUser) throws IOException {
        ResetEntity resetEntity = new ResetEntity();
        resetEntity.setIdUser(idUser);
        resetPasswordApiService.sendResetPasswordEmail(resetEntity);
    }
}
