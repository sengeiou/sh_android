package com.shootr.android.ui.views;

import java.io.File;

public interface PostNewShotView extends LoadDataView {

    void setResultOk();

    void closeScreen();

    void setRemainingCharactersCount(int remainingCharacters);

    void setRemainingCharactersColorValid();

    void setRemainingCharactersColorInvalid();

    void enableSendButton();

    void disableSendButton();

    void hideKeyboard();

    void selectImage();

    void showImagePreview(String imagePath);

    void showImagePreview(File imageFile);

    void hideImagePreview();
}
