package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.views.base.DataTransferView;

public interface EmailRegistrationView extends DataTransferView {

    void showCreateButton();

    void hideCreateButton();

    String getEmail();

    String getUsername();

    String getPassword();

    void showEmailError(String errorMessage);

    void showUsernameError(String errorMessage);

    void showPasswordError(String errorMessage);

    void askEmailConfirmation();

    void focusOnEmailField();

    void focusOnPasswordField();

    void focusOnUsernameField();

    void navigateToWelcomePage();
}
