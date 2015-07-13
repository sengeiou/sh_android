package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.ForgotPasswordResult;
import com.shootr.android.ui.model.ForgotPasswordUserModel;
import javax.inject.Inject;

public class ForgotPasswordUserModelMapper {

    @Inject public ForgotPasswordUserModelMapper() {
    }

    public ForgotPasswordUserModel transform(ForgotPasswordResult forgotPasswordResult) {
        ForgotPasswordUserModel model = new ForgotPasswordUserModel();
        model.setIdUser(forgotPasswordResult.getIdUser());
        model.setUsername(forgotPasswordResult.getUserName());
        model.setEncryptedEmail(forgotPasswordResult.getEmailEncripted());
        model.setAvatarUrl(forgotPasswordResult.getPhoto());
        return model;
    }

}
