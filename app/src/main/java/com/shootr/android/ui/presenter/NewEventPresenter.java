package com.shootr.android.ui.presenter;

import com.shootr.android.R;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.NewEventInteractor;
import com.shootr.android.domain.validation.EventValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.model.EndDate;
import com.shootr.android.ui.model.FixedEndDate;
import com.shootr.android.ui.views.NewEventView;
import com.shootr.android.util.DateFormatter;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.TimeFormatter;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.MutableDateTime;
import org.joda.time.base.AbstractDateTime;
import timber.log.Timber;

public class NewEventPresenter implements Presenter {

    private static final int DEFAULT_END_DATETIME_ID = R.id.end_date_6_hours;

    private final DateFormatter dateFormatter;
    private final TimeFormatter timeFormatter;
    private final NewEventInteractor newEventInteractor;
    private final ErrorMessageFactory errorMessageFactory;

    private NewEventView newEventView;
    private List<EndDate> suggestedEndDates;

    private MutableDateTime selectedStartDateTime;
    private EndDate selectedEndDate;

    @Inject public NewEventPresenter(DateFormatter dateFormatter, TimeFormatter timeFormatter,
      NewEventInteractor newEventInteractor, ErrorMessageFactory errorMessageFactory) {
        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
        this.newEventInteractor = newEventInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    //region Initialization
    public void initialize(NewEventView newEventView, List<EndDate> suggestedEndDates) {
        this.newEventView = newEventView;
        this.suggestedEndDates = suggestedEndDates;
        this.setDefaultStartDateTime();
        this.setDefaultEndDateTime();
    }

    private void setDefaultStartDateTime() {
        MutableDateTime currentDateTime = new MutableDateTime();
        setStartDateTime(currentDateTime);
    }

    private void setDefaultEndDateTime() {
        setEndDateTime(defaultEndDateTime());
    }

    private EndDate defaultEndDateTime() {
        return endDateFromItemId(DEFAULT_END_DATETIME_ID);
    }
    //endregion

    //region Interaction methods
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
        FixedEndDate endDate = new FixedEndDate(selectedTimestamp, R.id.end_date_custom, timeFormatter, dateFormatter);
        setEndDateTime(endDate);
    }

    public void done() {
        long startTimestamp = selectedStartDateTime.getMillis();
        long endTimestamp = selectedEndDate.getDateTime(startTimestamp);
        String title = filterTitle(newEventView.getEventTitle());
        newEventInteractor.createNewEvent(title, startTimestamp, endTimestamp,
          new NewEventInteractor.Callback() {
              @Override public void onLoaded(Event event) {
                  Timber.i("Wiii, evento cargado: %s", event.getTitle());
              }
          }, new Interactor.InteractorErrorCallback() {
              @Override public void onError(ShootrException error) {
                  if (error instanceof DomainValidationException) {
                      DomainValidationException validationException = (DomainValidationException) error;
                      List<FieldValidationError> errors = validationException.getErrors();
                      showValidationErrors(errors);
                  }
                  Timber.w("Wooo, error: %s", error.getClass().getSimpleName());
              }
          });
    }

    //endregion
    private void showValidationErrors(List<FieldValidationError> errors) {
        for (FieldValidationError validationError : errors) {
            String errorMessage = errorMessageFactory.getMessageForCode(validationError.getErrorCode());
            switch (validationError.getField()) {
                case EventValidator.FIELD_TITLE:
                    showViewTitleError(errorMessage);
                    break;
                case EventValidator.FIELD_START_DATE:
                    showViewStartDateError(errorMessage);
                    break;
                case EventValidator.FIELD_END_DATE:
                    showViewEndDateError(errorMessage);
                    break;
                default:
                    showViewError(errorMessage);
            }
        }
    }

    //region Errors
    private void showViewTitleError(String errorMessage) {
        newEventView.showTitleError(errorMessage);
    }

    private void showViewStartDateError(String errorMessage) {
        newEventView.showStartDateError(errorMessage);
    }

    private void showViewEndDateError(String errorMessage) {
        newEventView.showEndDateError(errorMessage);
    }

    private void showViewError(String errorMessage) {
        newEventView.showError(errorMessage);
    }
    //endregion

    private String filterTitle(String title) {
        return title.trim();
    }

    private void setStartDateTime(MutableDateTime dateTime) {
        selectedStartDateTime = dateTime;
        this.setViewStartDateTime(dateTime);
    }

    private void setEndDateTime(EndDate endDate) {
        selectedEndDate = endDate;
        newEventView.setEndDate(endDate.getTitle());
    }

    private void setViewStartDateTime(AbstractDateTime dateTime) {
        long selectedDateTimeMillis = dateTime.getMillis();
        newEventView.setStartDate(dateFormatter.getAbsoluteDate(selectedDateTimeMillis));
        newEventView.setStartTime(timeFormatter.getAbsoluteTime(selectedDateTimeMillis));
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
