package com.shootr.android.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.presenter.NewEventPresenter;
import com.shootr.android.ui.views.NewEventView;
import com.shootr.android.ui.widgets.DatePickerBuilder;
import com.shootr.android.ui.widgets.TimePickerBuilder;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import javax.inject.Inject;

public class NewEventActivity extends BaseActivity implements NewEventView {

    @Inject NewEventPresenter presenter;

    @InjectView(R.id.new_event_start_date) TextView startDateView;
    @InjectView(R.id.new_event_start_time) TextView startTimeView;
    @InjectView(R.id.new_event_end_date) TextView endDateView;

    private PopupMenu endDatePopupMenu;

    //region Initialization
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_new_event);
        initializeViews();
        setupActionbar();
        initializePresenter();
    }

    private void initializeViews() {
        ButterKnife.inject(this);
        endDatePopupMenu = new PopupMenu(this, endDateView);
        endDatePopupMenu.inflate(R.menu.new_event_end_date);
        endDatePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                presenter.endDateItemSelected(itemId, menuItem.getTitle().toString());
                return true;
            }
        });
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void initializePresenter() {
        presenter.initialize(this);
    }
    //endregion

    @OnClick(R.id.new_event_start_date)
    public void onStartDateClick() {
        DatePickerDialog datePickerDialog = DatePickerBuilder.builder()
          .listener(new DatePickerBuilder.DateListener() {
              @Override public void onDateSelected(int year, int month, int day) {
                  presenter.startDateSelected(year, month, day);
              }
          }).build();
        datePickerDialog.show(getSupportFragmentManager(), "datepicker");
    }

    @OnClick(R.id.new_event_start_time)
    public void onStartTimeClick() {
        TimePickerDialog timePickerDialog = TimePickerBuilder.builder()
          .listener(new TimePickerBuilder.TimeListener() {
              @Override public void onTimeSelected(int hour, int minute) {
                  presenter.startTimeSelected(hour, minute);
              }
          }).build();
        timePickerDialog.show(getSupportFragmentManager(), "timepicker");
    }

    @OnClick(R.id.new_event_end_date)
    public void onEndDateClick() {
        endDatePopupMenu.show();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //region View Methods
    @Override public void setStartDate(String dateText) {
        startDateView.setText(dateText);
    }

    @Override public void setStartTime(String timeText) {
        startTimeView.setText(timeText);
    }

    @Override public void setEndDate(String timeText) {
        endDateView.setText(timeText);
    }

    @Override public void pickCustomDateTime() {
        //TODO open dialog activity
    }
    //endregion


}
