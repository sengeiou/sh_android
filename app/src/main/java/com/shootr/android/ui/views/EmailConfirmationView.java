package com.shootr.android.ui.views;

public interface EmailConfirmationView {

    void showConfirmationToUser(String email);

    void showUserEmail(String userEmail);

    void showError(String errorMessage);

    void showEmailError(String errorMessage);

    void updateDoneButton();

    void hideDoneButton();
}
