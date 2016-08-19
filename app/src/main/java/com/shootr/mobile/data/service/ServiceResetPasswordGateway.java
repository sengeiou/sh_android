package com.shootr.mobile.data.service;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.ResetPasswordApiService;
import com.shootr.mobile.data.entity.ForgotPasswordEntity;
import com.shootr.mobile.data.entity.ForgotPasswordResultEntity;
import com.shootr.mobile.data.mapper.ForgotPasswordResultEntityMapper;
import com.shootr.mobile.domain.model.user.ForgotPasswordResult;
import com.shootr.mobile.domain.exception.InvalidForgotPasswordException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.service.user.ResetPasswordGateway;
import java.io.IOException;
import javax.inject.Inject;

public class ServiceResetPasswordGateway implements ResetPasswordGateway {

    private final ResetPasswordApiService resetPasswordApiService;
    private final ForgotPasswordResultEntityMapper forgotPasswordResultEntityMapper;

    @Inject public ServiceResetPasswordGateway(ResetPasswordApiService resetPasswordApiService,
      ForgotPasswordResultEntityMapper forgotPasswordResultEntityMapper) {
        this.resetPasswordApiService = resetPasswordApiService;
        this.forgotPasswordResultEntityMapper = forgotPasswordResultEntityMapper;
    }

    @Override public ForgotPasswordResult performPasswordReset(String usernameOrEmail)
      throws InvalidForgotPasswordException {
        ForgotPasswordEntity forgotPasswordEntity = new ForgotPasswordEntity();
        forgotPasswordEntity.setUserNameOrEmail(usernameOrEmail);
        ForgotPasswordResultEntity forgotPasswordResultEntity;
        try {
            forgotPasswordResultEntity = resetPasswordApiService.passwordReset(forgotPasswordEntity);
        } catch (ApiException error) {
            throw new InvalidForgotPasswordException(error);
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
        return forgotPasswordResultEntityMapper.transform(forgotPasswordResultEntity);
    }
}
