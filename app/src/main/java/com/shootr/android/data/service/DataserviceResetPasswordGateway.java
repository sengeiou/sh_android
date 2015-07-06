package com.shootr.android.data.service;

import com.shootr.android.data.api.service.ResetPasswordApiService;
import com.shootr.android.data.entity.ForgotPasswordEntity;
import com.shootr.android.data.entity.ForgotPasswordResultEntity;
import com.shootr.android.data.mapper.ForgotPasswordResultEntityMapper;
import com.shootr.android.domain.ForgotPasswordResult;
import com.shootr.android.domain.service.user.ResetPasswordGateway;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceResetPasswordGateway implements ResetPasswordGateway {

    private final ResetPasswordApiService resetPasswordApiService;
    private final ForgotPasswordResultEntityMapper forgotPasswordResultEntityMapper;

    @Inject public DataserviceResetPasswordGateway(ResetPasswordApiService resetPasswordApiService,
      ForgotPasswordResultEntityMapper forgotPasswordResultEntityMapper) {
        this.resetPasswordApiService = resetPasswordApiService;
        this.forgotPasswordResultEntityMapper = forgotPasswordResultEntityMapper;
    }

    @Override public ForgotPasswordResult performPasswordReset(String usernameOrEmail) throws IOException {
        ForgotPasswordEntity forgotPasswordEntity = new ForgotPasswordEntity();
        forgotPasswordEntity.setUserNameOrEmail(usernameOrEmail);
        ForgotPasswordResultEntity forgotPasswordResultEntity = resetPasswordApiService.passwordReset(forgotPasswordEntity);
        return forgotPasswordResultEntityMapper.transform(forgotPasswordResultEntity);
    }
}
