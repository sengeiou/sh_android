package com.shootr.android.ui.views;

public interface ResetPasswordConfirmationView {

    void showAvatar(String avatarUrl);

    void showUsername(String username);

    void showEmail(String email);

    void hideConfirmationButton();

    void showDoneButton();

}
