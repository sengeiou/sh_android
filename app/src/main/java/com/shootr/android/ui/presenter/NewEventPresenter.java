package com.shootr.android.ui.presenter;

import com.shootr.android.R;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetEventInteractor;
import com.shootr.android.domain.interactor.event.NewEventInteractor;
import com.shootr.android.domain.validation.EventValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.ui.model.EndDate;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.FixedEndDate;
import com.shootr.android.ui.model.mappers.EventModelMapper;
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
    private final GetEventInteractor getEventInteractor;
    private final EventModelMapper eventModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private NewEventView newEventView;
    private List<EndDate> suggestedEndDates;

    private MutableDateTime selectedStartDateTime;
    private EndDate selectedEndDate;
    private String preloadedTitle;
    private long preloadedStartDate;
    private EndDate preloadedEndDate;

    @Inject public NewEventPresenter(DateFormatter dateFormatter, TimeFormatter timeFormatter,
      NewEventInteractor newEventInteractor, GetEventInteractor getEventInteractor, EventModelMapper eventModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
        this.newEventInteractor = newEventInteractor;
        this.getEventInteractor = getEventInteractor;
        this.eventModelMapper = eventModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    //region Initialization
    public void initialize(NewEventView newEventView, List<EndDate> suggestedEndDates, long optionalIdEventToEdit) {
        this.newEventView = newEventView;
        this.suggestedEndDates = suggestedEndDates;
        if (optionalIdEventToEdit == 0L) {
            this.setDefaultStartDateTime();
            this.setDefaultEndDateTime();
        } else {
            this.preloadEventToEdit(optionalIdEventToEdit);
        }
    }

    private void preloadEventToEdit(long optionalIdEventToEdit) {
        getEventInteractor.loadEvent(optionalIdEventToEdit, new GetEventInteractor.Callback() {
            @Override public void onLoaded(Event event) {
                setDefaultEventInfo(eventModelMapper.transform(event));
            }
        });
    }

    private void setDefaultEventInfo(EventModel eventModel) {
        preloadedTitle = eventModel.getTitle();
        newEventView.setEventTitle(preloadedTitle);

        preloadedStartDate = eventModel.getStartDate();
        newEventView.setStartDate(dateFormatter.getAbsoluteDate(preloadedStartDate));
        newEventView.setStartTime(timeFormatter.getAbsoluteTime(preloadedStartDate));

        preloadedEndDate = endDateFromTimestamp(eventModel.getEndDate(), preloadedStartDate);
        newEventView.setEndDate(preloadedEndDate.getTitle());
    }

    private void setDefaultStartDateTime() {
        MutableDateTime currentDateTime = new MutableDateTime();
        roundDateUp(currentDateTime);
        setStartDateTime(currentDateTime);
    }

    private void roundDateUp(MutableDateTime currentDateTime) {
        if (currentDateTime.getMinuteOfHour()!=0) {
            currentDateTime.setMinuteOfHour(0);
            currentDateTime.setHourOfDay(currentDateTime.getHourOfDay() + 1);
        }
    }

    private void setDefaultEndDateTime() {
        setEndDateTime(defaultEndDateTime());
    }

    private EndDate defaultEndDateTime() {
        return endDateFromItemId(DEFAULT_END_DATETIME_ID);
    }
    //endregion

    //region Interaction methods
    public void titleTextChanged(String title) {
        boolean canCreateEvent = !(filterTitle(title).length() < 3);
        newEventView.doneButtonEnabled(canCreateEvent);
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
        FixedEndDate endDate = new FixedEndDate(selectedTimestamp, R.id.end_date_custom, timeFormatter, dateFormatter);
        setEndDateTime(endDate);
    }

    public void done() {
        newEventView.hideKeyboard();
        newEventView.showLoading();
        this.createEvent();
    }

    private void createEvent() {
        long startTimestamp = selectedStartDateTime.getMillis();
        long endTimestamp = selectedEndDate.getDateTime(startTimestamp);
        String title = filterTitle(newEventView.getEventTitle());
        newEventInteractor.createNewEvent(title, startTimestamp, endTimestamp, new NewEventInteractor.Callback() {
              @Override public void onLoaded(Event event) {
                  eventCreated(event);
              }
          }, new Interactor.InteractorErrorCallback() {
              @Override public void onError(ShootrException error) {
                  eventCreationError(error);
              }
          });
    }

    private void eventCreated(Event event) {
        newEventView.closeScreenWithResult(event.getId());
    }

    private void eventCreationError(ShootrException error) {
        newEventView.hideLoading();
        if (error instanceof DomainValidationException) {
            DomainValidationException validationException = (DomainValidationException) error;
            List<FieldValidationError> errors = validationException.getErrors();
            showValidationErrors(errors);
        }else if (error instanceof ServerCommunicationException) {
            onCommunicationError();
        } else {
            //TODO more error type handling
            Timber.e(error, "Unhandled error creating event.");
            showViewError(errorMessageFactory.getUnknownErrorMessage());
        }
    }

    private void onCommunicationError() {
        showViewError(errorMessageFactory.getCommunicationErrorMessage());
    }

    //endregion

    //region Errors
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

    private EndDate endDateFromTimestamp(long endDateTime, long startDateTime) {
        for (EndDate endDate : suggestedEndDates) {
            if (endDate.getDateTime(startDateTime) == endDateTime) {
                return endDate;
            }
        }
        return new FixedEndDate(endDateTime, R.id.end_date_custom, timeFormatter, dateFormatter);
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
