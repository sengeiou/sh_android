package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.views.base.DataTransferView;

public interface NewStreamView extends DataTransferView {

    void setStreamTitle(String title);

    String getStreamTitle();

    void showTitleError(String errorMessage);

    void closeScreenWithResult(String streamId);

    void closeScreenWithExitStream();

    void doneButtonEnabled(boolean enable);

    void hideKeyboard();

    void showNotificationConfirmation();

    void showRemoveStreamButton();

    void askRemoveStreamConfirmation();

    void showShortTitle(String currentShortTitle);

    String getStreamShortTitle();

    String getStreamDescription();

    void showDescription(String description);

    void showRestoreStreamButton();
}
