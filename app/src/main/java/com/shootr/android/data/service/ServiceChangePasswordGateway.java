package com.shootr.android.data.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ChangePasswordApiService;
import com.shootr.android.data.entity.ChangePasswordEntity;
import com.shootr.android.domain.exception.InvalidPasswordException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.service.user.ChangePasswordGateway;
import java.io.IOException;
import javax.inject.Inject;

public class ServiceChangePasswordGateway implements ChangePasswordGateway {

    private final ChangePasswordApiService changePasswordApiService;

    @Inject public ServiceChangePasswordGateway(ChangePasswordApiService changePasswordApiService) {
        this.changePasswordApiService = changePasswordApiService;
    }

    @Override public void changePassword(String currentPassword, String newPassword) throws InvalidPasswordException {
        ChangePasswordEntity changePasswordEntity = new ChangePasswordEntity();
        changePasswordEntity.setPassword(currentPassword);
        changePasswordEntity.setNewPassword(newPassword);
        try {
            changePasswordApiService.changePassword(changePasswordEntity);
        } catch (ApiException e) {
            throw new InvalidPasswordException(e);
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
    }
}
