package com.shootr.android.data.service;

import com.shootr.android.data.entity.ForgotPasswordResultEntity;
import com.shootr.android.data.mapper.ForgotPasswordResultEntityMapper;
import com.shootr.android.domain.ForgotPasswordResult;
import com.shootr.android.domain.service.user.ResetPasswordGateway;
import com.shootr.android.service.ShootrService;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceResetPasswordGateway implements ResetPasswordGateway {

    private final ShootrService shootrService;
    private final ForgotPasswordResultEntityMapper forgotPasswordResultEntityMapper;

    @Inject public DataserviceResetPasswordGateway(ShootrService shootrService,
      ForgotPasswordResultEntityMapper forgotPasswordResultEntityMapper) {
        this.shootrService = shootrService;
        this.forgotPasswordResultEntityMapper = forgotPasswordResultEntityMapper;
    }

    @Override public ForgotPasswordResult performPasswordReset(String usernameOrEmail) throws IOException {
        ForgotPasswordResultEntity forgotPasswordResultEntity = shootrService.passwordReset(usernameOrEmail);
        ForgotPasswordResult forgotPasswordResult = forgotPasswordResultEntityMapper.transform(forgotPasswordResultEntity);
        return forgotPasswordResult;
    }
}
