package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.ForgotPasswordResultEntity;
import com.shootr.android.domain.ForgotPasswordResult;
import javax.inject.Inject;

public class ForgotPasswordResultEntityMapper {

    private UserAvatarUrlProvider userAvatarUrlProvider;

    @Inject public ForgotPasswordResultEntityMapper(UserAvatarUrlProvider userAvatarUrlProvider) {
        this.userAvatarUrlProvider = userAvatarUrlProvider;
    }

    public ForgotPasswordResult transform(ForgotPasswordResultEntity forgotPasswordResultEntity) {
        ForgotPasswordResult forgotPasswordResult = new ForgotPasswordResult();
        forgotPasswordResult.setIdUser(forgotPasswordResultEntity.getIdUser());
        forgotPasswordResult.setEmailEncripted(forgotPasswordResultEntity.getEmailEncrypted());
        forgotPasswordResult.setUserName(forgotPasswordResultEntity.getUserName());
        forgotPasswordResult.setAvatar(userAvatarUrlProvider.thumbnail(forgotPasswordResultEntity.getIdUser()));
        return forgotPasswordResult;
    }
}
