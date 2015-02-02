package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.NewEventView;
import com.shootr.android.util.DateFormatter;
import com.shootr.android.util.TimeFormatter;
import javax.inject.Inject;
import org.joda.time.MutableDateTime;
import org.joda.time.base.AbstractDateTime;

public class NewEventPresenter implements Presenter {

    private final DateFormatter dateFormatter;
    private final TimeFormatter timeFormatter;

    private NewEventView newEventView;
    private MutableDateTime selectedDateTime;

    @Inject public NewEventPresenter(DateFormatter dateFormatter, TimeFormatter timeFormatter) {
        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
    }

    public void initialize(NewEventView newEventView) {
        this.newEventView = newEventView;
        this.setStartDateTimeCurrent();
    }

    private void setStartDateTimeCurrent() {
        MutableDateTime currentDateTime = new MutableDateTime();
        setStartDateTime(currentDateTime);
    }

    private void setStartDateTime(MutableDateTime dateTime) {
        selectedDateTime = dateTime;
        this.setViewStartDateTime(dateTime);
    }

    private void setViewStartDateTime(AbstractDateTime dateTime) {
        long selectedDateTimeMillis = dateTime.getMillis();
        newEventView.setStartDate(dateFormatter.getAbsoluteDate(selectedDateTimeMillis));
        newEventView.setStartTime(timeFormatter.getAbsoluteTime(selectedDateTimeMillis));
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }


    public void startDateSelected(int year, int month, int day) {
        selectedDateTime.setYear(year);
        selectedDateTime.setMonthOfYear(month);
        selectedDateTime.setDayOfMonth(day);
        this.setViewStartDateTime(selectedDateTime);
    }

    public void startTimeSelected(int hour, int minutes) {
        selectedDateTime.setHourOfDay(hour);
        selectedDateTime.setMinuteOfHour(minutes);
        this.setViewStartDateTime(selectedDateTime);
    }
}
