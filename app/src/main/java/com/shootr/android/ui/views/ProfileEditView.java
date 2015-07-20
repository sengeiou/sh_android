package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;

public interface ProfileEditView {

    void renderUserInfo(UserModel currentUserModel);

    void hideKeyboard();

    void showUpdatedSuccessfulAlert();

    void closeScreen();

    String getUsername();

    String getName();

    String getBio();

    String getWebsite();

    void showUsernameValidationError(String errorMessage);

    void showNameValidationError(String errorMessage);

    void showWebsiteValidationError(String errorMessage);

    void showBioValidationError(String errorMessage);

    void showDiscardConfirmation();

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void alertComunicationError();

    void alertConnectionNotAvailable();

    void showEmailNotConfirmedError(String error);

    void showError(String errorMessage);
}
