package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.ForgotPasswordResultEntity;
import com.shootr.mobile.domain.ForgotPasswordResult;

import javax.inject.Inject;

public class ForgotPasswordResultEntityMapper {

    @Inject public ForgotPasswordResultEntityMapper() {
    }

    public ForgotPasswordResult transform(ForgotPasswordResultEntity forgotPasswordResultEntity) {
        ForgotPasswordResult forgotPasswordResult = new ForgotPasswordResult();
        forgotPasswordResult.setIdUser(forgotPasswordResultEntity.getIdUser());
        forgotPasswordResult.setEmailEncripted(forgotPasswordResultEntity.getEmailEncrypted());
        forgotPasswordResult.setUserName(forgotPasswordResultEntity.getUserName());
        forgotPasswordResult.setPhoto(forgotPasswordResultEntity.getPhoto());
        return forgotPasswordResult;
    }
}
