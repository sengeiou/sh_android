package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ForgotPasswordUserModel;
import com.shootr.mobile.ui.views.base.DataTransferView;

public interface ResetPasswordRequestView extends DataTransferView {

    void enableNextButton();

    void disableNextButton();

    void navigateToResetPasswordConfirmation(ForgotPasswordUserModel forgotPasswordUserModel);

    void showResetPasswordError();

    void hideKeyboard();

    String getUsernameOrEmail();
}
