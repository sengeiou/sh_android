package com.shootr.android.ui.views;

import com.shootr.android.ui.views.base.DataTransferView;

import java.util.Locale;

public interface NewEventView extends DataTransferView{

    void setStartDate(String dateText);

    void setStartTime(String timeText);

    void setEventTitle(String title);

    void setTimeZone(String timezoneName);

    String getEventTitle();

    void showTitleError(String errorMessage);

    void showStartDateError(String errorMessage);

    void closeScreenWithResult(String eventId, String title);

    void doneButtonEnabled(boolean enable);

    void hideKeyboard();

    void navigateToPickTimezone(String currentTimezoneID);

    void showNotificationConfirmation();
}
