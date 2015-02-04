package com.shootr.android.ui.widgets;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import java.util.Calendar;
import org.joda.time.DateTime;

public class DatePickerBuilder {

    private DateTime dateTime;
    private DateListener listener;

    public DatePickerBuilder() {
        dateTime = DateTime.now();
    }

    public static DatePickerBuilder builder() {
        return new DatePickerBuilder();
    }

    public DatePickerBuilder listener(DateListener listener) {
        this.listener = listener;
        return this;
    }

    public DatePickerBuilder initial(long timestamp) {
        dateTime = new DateTime(timestamp);
        return this;
    }

    public DatePickerDialog build() {
        return DatePickerDialog.newInstance(createOnDateSetListenerAdapter(), dateTime.getYear(),
          dateTime.getMonthOfYear()-1, dateTime.getDayOfMonth(), false);
    }

    private DatePickerDialog.OnDateSetListener createOnDateSetListenerAdapter() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                listener.onDateSelected(year, month+1, day);
            }
        };
    }

    public static interface DateListener {

        void onDateSelected(int year, int month, int day);
    }
}
