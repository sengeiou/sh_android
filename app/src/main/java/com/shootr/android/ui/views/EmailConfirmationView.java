package com.shootr.android.ui.views;

public interface EmailConfirmationView {

    void showConfirmationAlertToUser(String email);

    void showUserEmail(String userEmail);

    void showError(String errorMessage);

    void showEmailError(String errorMessage);

    void showDoneButton();

    void hideDoneButton();

    void closeScreen();
}
