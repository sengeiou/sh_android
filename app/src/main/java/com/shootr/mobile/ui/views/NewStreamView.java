package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.views.base.DataTransferView;

public interface NewStreamView extends DataTransferView {

    void setStreamTitle(String title);

    void showTitleError(String errorMessage);

    void closeScreenWithResult(String streamId);

    void doneButtonEnabled(boolean enable);

    void hideKeyboard();

    void showNotificationConfirmation();

    void showDescription(String description);

    void setModeValue(Integer readWriteMode);
}
