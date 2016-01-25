package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.DataTransferView;
import java.io.File;
import java.util.List;

public interface PostNewShotView extends DataTransferView {

    void performBackPressed();

    void showDiscardAlert();

    void setResultOk();

    void closeScreen();

    void setRemainingCharactersCount(int remainingCharacters);

    void setRemainingCharactersColorValid();

    void setRemainingCharactersColorInvalid();

    void enableSendButton();

    void disableSendButton();

    void showSendButton();

    void hideSendButton();

    void hideKeyboard();

    void showImagePreview(File imageFile);

    void hideImagePreview();

    void takePhotoFromCamera();

    void choosePhotoFromGallery();

    void showReplyToUsername(String replyToUsername);

    void renderMentionSuggestions(List<UserModel> mentionSuggestions);

    void showMentionSuggestions();

    void hideMentionSuggestions();
}
