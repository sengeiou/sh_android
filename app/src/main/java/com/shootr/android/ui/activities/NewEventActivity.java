package com.shootr.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
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
import com.shootr.android.ui.base.BaseActivity;
import com.shootr.android.ui.model.EndDate;
import com.shootr.android.ui.model.RelativeEndDate;
import com.shootr.android.ui.presenter.NewEventPresenter;
import com.shootr.android.ui.views.NewEventView;
import com.shootr.android.ui.widgets.DatePickerBuilder;
import com.shootr.android.ui.widgets.FloatLabelLayout;
import com.shootr.android.ui.widgets.TimePickerBuilder;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class NewEventActivity extends BaseActivity implements NewEventView {

    private static final long TIME_1_DAY_MILLIS = 24 * 60 * 60 * 1000;
    private static final long TIME_6_HOURS_MILLIS = 6 * 60 * 60 * 1000;
    private static final long TIME_30_MINUTES_MILLIS = 30 * 60 * 1000;

    @Inject NewEventPresenter presenter;

    @InjectView(R.id.new_event_title) EditText titleView;
    @InjectView(R.id.new_event_title_label) FloatLabelLayout titleLabelView;
    @InjectView(R.id.new_event_start_date) TextView startDateView;
    @InjectView(R.id.new_event_start_time) TextView startTimeView;
    @InjectView(R.id.new_event_end_date) TextView endDateView;
    @InjectView(R.id.new_event_title_error) TextView titleErrorView;
    @InjectView(R.id.new_event_start_date_error) TextView startDateErrorView;
    @InjectView(R.id.new_event_end_date_error) TextView endDateErrorView;

    private PopupMenu endDatePopupMenu;
    private MenuItem doneMenuItem;

    //region Initialization
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerContent(R.layout.activity_new_event);
        long idEventToEdit = getIntent().getLongExtra(EventsListActivity.KEY_EVENT_ID, 0L);

        initializeViews(idEventToEdit);
        setupActionbar(idEventToEdit);
        initializePresenter(idEventToEdit);
    }

    private void initializeViews(long idEventToEdit) {
        ButterKnife.inject(this);
        endDatePopupMenu = new PopupMenu(this, endDateView);
        endDatePopupMenu.inflate(R.menu.new_event_end_date);
        endDatePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                presenter.endDateItemSelected(itemId);
                return true;
            }
        });
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

    private void setupActionbar(long idEventToEdit) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);

        if (idEventToEdit > 0) {
            actionBar.setTitle(R.string.activity_edit_event_title);
        }
    }

    private void initializePresenter(long idEventToEdit) {
        presenter.initialize(this, suggestedEndDates(), idEventToEdit);
    }

    private List<EndDate> suggestedEndDates() {
        List<EndDate> endDates = new ArrayList<>();
        endDates.add(new RelativeEndDate(getString(R.string.end_date_30_minutes), R.id.end_date_30_minutes,
          TIME_30_MINUTES_MILLIS));
        endDates.add(
          new RelativeEndDate(getString(R.string.end_date_6_hours), R.id.end_date_6_hours, TIME_6_HOURS_MILLIS));
        endDates.add(new RelativeEndDate(getString(R.string.end_date_1_day), R.id.end_date_1_day, TIME_1_DAY_MILLIS));
        return endDates;
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

    @OnClick(R.id.new_event_end_date)
    public void onEndDateClick() {
        endDatePopupMenu.show();
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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            long selectedTimestamp = data.getLongExtra(DateTimePickerDialogActivity.KEY_TIMESTAMP, 0L);
            if (selectedTimestamp != 0L) {
                presenter.customEndDateSelected(selectedTimestamp);
            }
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

    @Override public void setEndDate(String timeText) {
        resetEndDateError();
        endDateView.setText(timeText);
    }

    @Override public void pickCustomDateTime(long initialTimestamp) {
        Intent dateTimePickerIntent = new Intent(this, DateTimePickerDialogActivity.class);
        dateTimePickerIntent.putExtra(DateTimePickerDialogActivity.KEY_TIMESTAMP, initialTimestamp);
        startActivityForResult(dateTimePickerIntent, 1);
    }

    @Override public void setEventTitle(String title) {
        titleLabelView.showLabelWithoutAnimation();
        titleView.setText(title);
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

    @Override public void showEndDateError(String errorMessage) {
        endDateErrorView.setText(errorMessage);
    }

    @Override public void closeScreenWithResult(Long eventId) {
        setResult(RESULT_OK, new Intent().putExtra(EventsListActivity.KEY_EVENT_ID, eventId));
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

    private void resetEndDateError() {
        endDateErrorView.setText(null);
    }
    //endregion
}
