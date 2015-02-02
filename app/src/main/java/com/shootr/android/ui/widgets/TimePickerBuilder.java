package com.shootr.android.ui.widgets;

import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import org.joda.time.DateTime;

public class TimePickerBuilder {

    private final DateTime dateTime;
    private TimeListener listener;

    public static TimePickerBuilder builder() {
        return new TimePickerBuilder();
    }

    public TimePickerBuilder() {
        dateTime = new DateTime();
    }

    public TimePickerBuilder listener(TimeListener listener) {
        this.listener = listener;
        return this;
    }

    public TimePickerDialog build() {
        return TimePickerDialog.newInstance(createTimeListenerAdapter(), dateTime.getHourOfDay(),
          dateTime.getMinuteOfHour(), true, false);
    }

    private TimePickerDialog.OnTimeSetListener createTimeListenerAdapter() {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                listener.onTimeSelected(hour, minute);
            }
        };
    }

    public static interface TimeListener {

        void onTimeSelected(int hour, int minute);
    }
}
