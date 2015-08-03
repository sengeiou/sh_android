package com.shootr.android.ui.views;

import com.shootr.android.ui.views.base.DataTransferView;

public interface NewStreamView extends DataTransferView{

    void setStreamTitle(String title);

    String getStreamTitle();

    void showTitleError(String errorMessage);

    void closeScreenWithResult(String streamId, String title);

    void closeScreenWithExitStream();

    void doneButtonEnabled(boolean enable);

    void hideKeyboard();

    void showNotificationConfirmation();

    void showDeleteStreamButton();

    void askDeleteStreamConfirmation();

    void showShortTitle(String currentShortTitle);

    String getStreamShortTitle();

    String getStreamDescription();
}
