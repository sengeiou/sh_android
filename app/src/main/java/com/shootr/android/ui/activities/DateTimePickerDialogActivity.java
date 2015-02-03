package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.widgets.DatePickerBuilder;
import com.shootr.android.ui.widgets.TimePickerBuilder;
import com.shootr.android.util.DateFormatter;
import com.shootr.android.util.TimeFormatter;
import javax.inject.Inject;
import org.joda.time.MutableDateTime;

public class DateTimePickerDialogActivity extends BaseActivity
  implements DatePickerBuilder.DateListener, TimePickerBuilder.TimeListener {

    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_TIMEZONE = "timezone";

    @InjectView(R.id.date_time_picker_date) TextView dateView;
    @InjectView(R.id.date_time_picker_time) TextView timeView;
    @InjectView(R.id.date_time_picker_timezone) TextView timezoneView;

    @Inject DateFormatter dateFormatter;
    @Inject TimeFormatter timeFormatter;

    private MutableDateTime selectedDateTime = new MutableDateTime();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_date_time_picker);
        ButterKnife.inject(this);

        Bundle intentExtras = getIntent().getExtras();
        setupInitialData(intentExtras);
    }

    private void setupInitialData(@Nullable Bundle parameters) {
        long timestamp = parameters != null ? parameters.getLong(KEY_TIMESTAMP, 0L) : 0L;
        if (timestamp == 0L) {
            timestamp = System.currentTimeMillis();
        }
        selectedDateTime.setMillis(timestamp);
        updateDateTimeText();

        String timezone = parameters != null ? parameters.getString(KEY_TIMEZONE) : null;
        setTimezoneOrHide(timezone);
    }

    private void setTimezoneOrHide(String timezone) {
        if (timezone != null) {
            timezoneView.setText(timezone);
        } else {
            timezoneView.setVisibility(View.GONE);
        }
    }

    private void updateDateTimeText() {
        dateView.setText(dateFormatter.getAbsoluteDate(selectedDateTime.getMillis()));
        timeView.setText(timeFormatter.getAbsoluteTime(selectedDateTime.getMillis()));
    }

    @OnClick(R.id.date_time_picker_date)
    public void onDateClick() {
        DatePickerBuilder.builder().listener(this).initial(selectedDateTime.getMillis()).build().show(getSupportFragmentManager(), "datepicker");
    }

    @OnClick(R.id.date_time_picker_time)
    public void onTimeClick() {
        TimePickerBuilder.builder().listener(this).initial(selectedDateTime.getMillis()).build().show(getSupportFragmentManager(), "timepicker");
    }

    @OnClick(R.id.date_time_picker_done)
    public void onDoneClick() {
        long selectedTimestamp = selectedDateTime.getMillis();
        Intent resultData = new Intent();
        resultData.putExtra(KEY_TIMESTAMP, selectedTimestamp);
        setResult(RESULT_OK, resultData);
        finish();
    }

    @Override public void onDateSelected(int year, int month, int day) {
        selectedDateTime.setYear(year);
        selectedDateTime.setMonthOfYear(month);
        selectedDateTime.setDayOfMonth(day);
        updateDateTimeText();
    }

    @Override public void onTimeSelected(int hour, int minute) {
        selectedDateTime.setHourOfDay(hour);
        selectedDateTime.setMinuteOfHour(minute);
        updateDateTimeText();
    }
}
