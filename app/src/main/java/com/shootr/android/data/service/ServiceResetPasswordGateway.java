package com.shootr.android.data.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ResetPasswordApiService;
import com.shootr.android.data.entity.ForgotPasswordEntity;
import com.shootr.android.data.entity.ForgotPasswordResultEntity;
import com.shootr.android.data.mapper.ForgotPasswordResultEntityMapper;
import com.shootr.android.domain.ForgotPasswordResult;
import com.shootr.android.domain.exception.InvalidForgotPasswordException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.service.user.ResetPasswordGateway;
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

    @Override public ForgotPasswordResult performPasswordReset(String usernameOrEmail) throws
      InvalidForgotPasswordException {
        ForgotPasswordEntity forgotPasswordEntity = new ForgotPasswordEntity();
        forgotPasswordEntity.setUserNameOrEmail(usernameOrEmail);
        ForgotPasswordResultEntity forgotPasswordResultEntity;
        try {
            forgotPasswordResultEntity = resetPasswordApiService.passwordReset(forgotPasswordEntity);
        } catch (ApiException error) {
            throw new InvalidForgotPasswordException();
        } catch (IOException error) {
            throw new ServerCommunicationException(error);
        }
        return forgotPasswordResultEntityMapper.transform(forgotPasswordResultEntity);
    }
}
