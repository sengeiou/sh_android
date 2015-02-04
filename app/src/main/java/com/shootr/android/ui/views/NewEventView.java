package com.shootr.android.ui.views;

import com.shootr.android.ui.views.base.DataTransferView;

public interface NewEventView extends DataTransferView{

    void setStartDate(String dateText);

    void setStartTime(String timeText);

    void setEndDate(String timeText);

    void pickCustomDateTime(long initialTimestamp);

    String getEventTitle();

    void showTitleError(String errorMessage);

    void showStartDateError(String errorMessage);

    void showEndDateError(String errorMessage);

    void closeScreenWithResult();
}
