package com.shootr.android.ui.presenter;

import com.shootr.android.R;
import com.shootr.android.ui.model.EndDate;
import com.shootr.android.ui.model.FixedEndDate;
import com.shootr.android.ui.views.NewEventView;
import com.shootr.android.util.DateFormatter;
import com.shootr.android.util.TimeFormatter;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.MutableDateTime;
import org.joda.time.base.AbstractDateTime;

public class NewEventPresenter implements Presenter {

    private static final int DEFAULT_END_DATETIME_ID = R.id.end_date_6_hours;

    private final DateFormatter dateFormatter;
    private final TimeFormatter timeFormatter;

    private NewEventView newEventView;
    private List<EndDate> suggestedEndDates;

    private MutableDateTime selectedStartDateTime;
    private EndDate selectedEndDate;

    @Inject public NewEventPresenter(DateFormatter dateFormatter, TimeFormatter timeFormatter) {
        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
    }

    public void initialize(NewEventView newEventView, List<EndDate> suggestedEndDates) {
        this.newEventView = newEventView;
        this.suggestedEndDates = suggestedEndDates;
        this.setDefaultStartDateTime();
        this.setDefaultEndDateTime();
    }

    private void setDefaultEndDateTime() {
        setEndDateTime(defaultEndDateTime());
    }

    private void setEndDateTime(EndDate endDate) {
        selectedEndDate = endDate;
        newEventView.setEndDate(endDate.getTitle());
    }

    private EndDate defaultEndDateTime() {
        return endDateFromItemId(DEFAULT_END_DATETIME_ID);
    }

    private void setDefaultStartDateTime() {
        MutableDateTime currentDateTime = new MutableDateTime();
        setStartDateTime(currentDateTime);
    }

    private void setStartDateTime(MutableDateTime dateTime) {
        selectedStartDateTime = dateTime;
        this.setViewStartDateTime(dateTime);
    }

    private void setViewStartDateTime(AbstractDateTime dateTime) {
        long selectedDateTimeMillis = dateTime.getMillis();
        newEventView.setStartDate(dateFormatter.getAbsoluteDate(selectedDateTimeMillis));
        newEventView.setStartTime(timeFormatter.getAbsoluteTime(selectedDateTimeMillis));
    }

    public void startDateSelected(int year, int month, int day) {
        selectedStartDateTime.setYear(year);
        selectedStartDateTime.setMonthOfYear(month);
        selectedStartDateTime.setDayOfMonth(day);
        this.setViewStartDateTime(selectedStartDateTime);
    }

    public void startTimeSelected(int hour, int minutes) {
        selectedStartDateTime.setHourOfDay(hour);
        selectedStartDateTime.setMinuteOfHour(minutes);
        this.setViewStartDateTime(selectedStartDateTime);
    }

    public void endDateItemSelected(int selectedItemId) {
        if (selectedItemId == R.id.end_date_custom) {
            newEventView.pickCustomDateTime(selectedEndDate.getDateTime(selectedStartDateTime.getMillis()));
        } else {
            EndDate endDate = endDateFromItemId(selectedItemId);
            setEndDateTime(endDate);
        }
    }

    public void customEndDateSelected(long selectedTimestamp) {
        FixedEndDate endDate =
          new FixedEndDate(selectedTimestamp, R.id.end_date_custom, timeFormatter, dateFormatter);
        setEndDateTime(endDate);
    }

    private EndDate endDateFromItemId(int itemId) {
        for (EndDate endDate : suggestedEndDates) {
            if (endDate.getMenuItemId() == itemId) {
                return endDate;
            }
        }
        return null;
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }

}
