package com.shootr.android.ui.widgets;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import java.util.Calendar;

public class DatePickerBuilder {

    private Calendar calendar;
    private DateListener listener;

    public DatePickerBuilder() {
        calendar = Calendar.getInstance();
    }

    public static DatePickerBuilder builder() {
        return new DatePickerBuilder();
    }

    public DatePickerBuilder listener(DateListener listener) {
        this.listener = listener;
        return this;
    }

    public DatePickerDialog build() {
        return DatePickerDialog.newInstance(createOnDateSetListenerAdapter(), calendar.get(Calendar.YEAR),
          calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
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
