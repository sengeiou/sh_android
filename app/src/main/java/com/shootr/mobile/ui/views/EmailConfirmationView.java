package com.shootr.mobile.ui.views;

public interface EmailConfirmationView {

    void showConfirmationEmailSentAlert(String email, Runnable alertCallback);

    void showUserEmail(String userEmail);

    void showError(String errorMessage);

    void showEmailError(String errorMessage);

    void showDoneButton();

    void hideDoneButton();

    void closeScreen();
}
