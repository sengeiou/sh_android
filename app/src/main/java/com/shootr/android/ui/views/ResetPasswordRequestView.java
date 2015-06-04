package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ForgotPasswordUserModel;
import com.shootr.android.ui.views.base.DataTransferView;

public interface ResetPasswordRequestView extends DataTransferView {

    void enableNextButton();

    void disableNextButton();

    void navigateToResetPasswordConfirmation(ForgotPasswordUserModel forgotPasswordUserModel);

    void showResetPasswordError();

    String getUsernameOrEmail();
}
