package com.shootr.android.ui.views;

import com.shootr.android.ui.views.base.DataTransferView;

public interface NewEventView extends DataTransferView{

    void setEventTitle(String title);

    String getEventTitle();

    void showTitleError(String errorMessage);

    void closeScreenWithResult(String eventId, String title);

    void doneButtonEnabled(boolean enable);

    void hideKeyboard();

    void showNotificationConfirmation();

    void showShortTitle(String currentShortTitle);

    void showShortTitleWarning();

    void hideShortTitleWarning();

    String getEventShortTitle();
}
