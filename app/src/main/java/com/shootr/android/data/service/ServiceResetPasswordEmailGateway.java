package com.shootr.android.data.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ResetPasswordApiService;
import com.shootr.android.data.entity.ResetPasswordEntity;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.service.user.ResetPasswordEmailGateway;
import java.io.IOException;
import javax.inject.Inject;

public class ServiceResetPasswordEmailGateway implements ResetPasswordEmailGateway {

    private final ResetPasswordApiService resetPasswordApiService;

    @Inject public ServiceResetPasswordEmailGateway(ResetPasswordApiService resetPasswordApiService) {
        this.resetPasswordApiService = resetPasswordApiService;
    }

    @Override public void sendPasswordResetEmail(String idUser) {
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        resetPasswordEntity.setIdUser(idUser);
        try {
            resetPasswordApiService.sendResetPasswordEmail(resetPasswordEntity);
        } catch (IOException | ApiException error) {
            throw new ServerCommunicationException(error);
        }
    }
}
