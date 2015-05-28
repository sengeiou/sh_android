package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.ForgotPasswordResultEntity;
import com.shootr.android.domain.ForgotPasswordResult;
import javax.inject.Inject;

public class ForgotPasswordResultEntityMapper {

    @Inject public ForgotPasswordResultEntityMapper() {

    }

    public ForgotPasswordResult transform(ForgotPasswordResultEntity forgotPasswordResultEntity) {
        ForgotPasswordResult forgotPasswordResult = new ForgotPasswordResult();
        forgotPasswordResult.setIdUser(forgotPasswordResultEntity.getIdUser());
        forgotPasswordResult.setEmailEncripted(forgotPasswordResultEntity.getEmailEncripted());
        forgotPasswordResult.setUserName(forgotPasswordResultEntity.getUserName());
        return forgotPasswordResult;
    }
}
