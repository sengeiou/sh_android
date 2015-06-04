package com.shootr.android.ui.views;

public interface ResetPasswordConfirmationView {

    void showAvatar(String avatarUrl);

    void showUsername(String username);

    void hideConfirmationButton();

    void showDoneButton();

    void showPostConfirmationMessage(String email);
}
