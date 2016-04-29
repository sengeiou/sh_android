package com.shootr.mobile.data.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ResetPasswordApiService;
import com.shootr.mobile.data.entity.ResetPasswordEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.service.user.ResetPasswordEmailGateway;
import java.io.IOException;
import javax.inject.Inject;

public class ServiceResetPasswordEmailGateway implements ResetPasswordEmailGateway {

    private final ResetPasswordApiService resetPasswordApiService;

    @Inject public ServiceResetPasswordEmailGateway(ResetPasswordApiService resetPasswordApiService) {
        this.resetPasswordApiService = resetPasswordApiService;
    }

    @Override public void sendPasswordResetEmail(String idUser, String locale) {
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        resetPasswordEntity.setIdUser(idUser);
        resetPasswordEntity.setLocale(locale);
        try {
            resetPasswordApiService.sendResetPasswordEmail(resetPasswordEntity);
        } catch (IOException | ApiException error) {
            throw new ServerCommunicationException(error);
        }
    }
}
