package com.shootr.android.ui.views;

import com.shootr.android.ui.views.base.DataTransferView;

public interface ResetPasswordConfirmationView extends DataTransferView{

    void showAvatar(String avatarUrl);

    void showUsername(String username);

    void hideConfirmationButton();

    void showDoneButton();

    void showPostConfirmationMessage(String email);
}
