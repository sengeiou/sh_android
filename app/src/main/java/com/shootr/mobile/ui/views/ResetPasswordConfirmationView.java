package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.views.base.DataTransferView;

public interface ResetPasswordConfirmationView extends DataTransferView {

    void showAvatar(String avatarUrl);

    void showUsername(String username);

    void hideConfirmationButton();

    void showConfirmationButton();

    void showDoneButton();

    void showPostConfirmationMessage(String email);

    void navigateToLogin();
}
