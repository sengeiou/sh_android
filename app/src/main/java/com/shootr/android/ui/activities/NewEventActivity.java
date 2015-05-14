package com.shootr.android.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.shootr.android.R;
import com.shootr.android.ui.base.BaseToolbarActivity;
import com.shootr.android.ui.presenter.NewEventPresenter;
import com.shootr.android.ui.views.NewEventView;
import com.shootr.android.ui.widgets.DatePickerBuilder;
import com.shootr.android.ui.widgets.FloatLabelLayout;
import com.shootr.android.ui.widgets.TimePickerBuilder;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import javax.inject.Inject;

public class NewEventActivity extends BaseToolbarActivity implements NewEventView {

    public static final String KEY_EVENT_ID = "event_id";
    public static final String KEY_EVENT_TITLE = "event_title";

    private static final int REQUEST_PICK_TIMEZONE = 2;

    @Inject NewEventPresenter presenter;

    @InjectView(R.id.new_event_title) EditText titleView;
    @InjectView(R.id.new_event_title_label) FloatLabelLayout titleLabelView;
    @InjectView(R.id.new_event_start_date) TextView startDateView;
    @InjectView(R.id.new_event_start_time) TextView startTimeView;
    @InjectView(R.id.new_event_title_error) TextView titleErrorView;
    @InjectView(R.id.new_event_start_date_timezone) TextView timezoneView;
    @InjectView(R.id.new_event_start_date_error) TextView startDateErrorView;

    private MenuItem doneMenuItem;

    //region Initialization
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_new_event);
        String idEventToEdit = getIntent().getStringExtra(KEY_EVENT_ID);

        initializeViews(idEventToEdit);
        setupActionbar(idEventToEdit);
        initializePresenter(idEventToEdit);
    }

    private void initializeViews(String idEventToEdit) {
        ButterKnife.inject(this);
        titleView.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                presenter.titleTextChanged(s.toString());
                resetTitleError();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void setupActionbar(String idEventToEdit) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);

        if (idEventToEdit != null) {
            actionBar.setTitle(R.string.activity_edit_event_title);
        }
    }

    private void initializePresenter(String idEventToEdit) {
        presenter.initialize(this, idEventToEdit);
    }

    //endregion

    //region Listeners
    @OnClick(R.id.new_event_start_date)
    public void onStartDateClick() {
        DatePickerDialog datePickerDialog = DatePickerBuilder.builder().listener(new DatePickerBuilder.DateListener() {
            @Override public void onDateSelected(int year, int month, int day) {
                presenter.startDateSelected(year, month, day);
            }
        }).build();
        datePickerDialog.show(getSupportFragmentManager(), "datepicker");
    }

    @OnClick(R.id.new_event_start_time)
    public void onStartTimeClick() {
        TimePickerDialog timePickerDialog = TimePickerBuilder.builder().listener(new TimePickerBuilder.TimeListener() {
            @Override public void onTimeSelected(int hour, int minute) {
                presenter.startTimeSelected(hour, minute);
            }
        }).build();
        timePickerDialog.show(getSupportFragmentManager(), "timepicker");
    }

    @OnClick(R.id.new_event_start_date_timezone)
    public void onTimezoneClick() {
        presenter.pickTimezone();
    }
    //endregion

    //region Activity methods
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_event, menu);
        doneMenuItem = menu.findItem(R.id.menu_done);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_done) {
            presenter.done();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_TIMEZONE && resultCode == RESULT_OK) {
            String selectedTimezone = data.getStringExtra(TimezonePickerActivity.KEY_TIMEZONE);
            presenter.timezoneSelected(selectedTimezone);
        }
    }
    //endregion

    //region View Methods
    @Override public void setStartDate(String dateText) {
        resetStartDateError();
        startDateView.setText(dateText);
    }

    @Override public void setStartTime(String timeText) {
        resetStartDateError();
        startTimeView.setText(timeText);
    }

    @Override public void setEventTitle(String title) {
        titleLabelView.showLabelWithoutAnimation();
        titleView.setText(title);
    }

    @Override public void setTimeZone(String timezoneName) {
        timezoneView.setText(timezoneName);
    }

    @Override public String getEventTitle() {
        return titleView.getText().toString();
    }

    @Override public void showTitleError(String errorMessage) {
        titleErrorView.setText(errorMessage);
    }

    @Override public void showStartDateError(String errorMessage) {
        startDateErrorView.setText(errorMessage);
    }

    @Override public void closeScreenWithResult(String eventId, String title) {
        setResult(RESULT_OK, new Intent() //
          .putExtra(KEY_EVENT_ID, eventId) //
          .putExtra(KEY_EVENT_TITLE, title));
        finish();
    }

    @Override public void doneButtonEnabled(boolean enable) {
        if (doneMenuItem != null) {
            doneMenuItem.setEnabled(enable);
        }
    }

    @Override public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titleView.getWindowToken(), 0);
    }

    @Override public void navigateToPickTimezone(String currentTimezoneID) {
        Intent intent = new Intent(this, TimezonePickerActivity.class);
        intent.putExtra(TimezonePickerActivity.KEY_TIMEZONE, currentTimezoneID);
        startActivityForResult(intent, REQUEST_PICK_TIMEZONE);
    }

    @Override public void showNotificationConfirmation() {
        new AlertDialog.Builder(this)
          .setMessage(getString(R.string.event_notification_confirmation_message))
          .setPositiveButton(getString(R.string.event_notification_confirmation_yes), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.confirmNotify(true);
              }
          })
          .setNegativeButton(getString(R.string.event_notification_confirmation_no), new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                  presenter.confirmNotify(false);
              }
          })
          .create().show();
    }

    @Override public void showLoading() {
        doneMenuItem.setActionView(R.layout.item_list_loading);
    }

    @Override public void hideLoading() {
        doneMenuItem.setActionView(null);
    }

    @Override public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetTitleError() {
        titleErrorView.setError(null);
    }

    private void resetStartDateError() {
        startDateErrorView.setText(null);
    }
    //endregion
}
