package com.shootr.android.ui.views;

import com.shootr.android.ui.views.base.DataTransferView;

public interface ResetPasswordConfirmationView extends DataTransferView{

    void showAvatar(String avatarUrl);

    void showUsername(String username);

    void hideConfirmationButton();

    void showConfirmationButton();

    void showDoneButton();

    void showPostConfirmationMessage(String email);

    void navigateToLogin();
}
