package com.shootr.android.ui.presenter;

import com.shootr.android.R;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.CreateEventInteractor;
import com.shootr.android.domain.interactor.event.GetEventInteractor;
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
import java.util.TimeZone;
import javax.inject.Inject;
import org.joda.time.MutableDateTime;
import org.joda.time.base.AbstractDateTime;
import timber.log.Timber;

public class NewEventPresenter implements Presenter {

    private static final int DEFAULT_END_DATETIME_ID = R.id.end_date_6_hours;
    public static final int MINIMUM_TITLE_LENGTH = 3;
    private static final long SIX_HOURS_MILLIS = 6 * 60 * 60 * 1000;

    private final DateFormatter dateFormatter;
    private final TimeFormatter timeFormatter;
    private final CreateEventInteractor createEventInteractor;
    private final GetEventInteractor getEventInteractor;
    private final EventModelMapper eventModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private NewEventView newEventView;
    private List<EndDate> suggestedEndDates;

    private boolean isNewEvent;
    private MutableDateTime selectedStartDateTime;
    private EndDate selectedEndDate;
    private TimeZone selectedTimeZone;
    private String preloadedTitle;
    private long preloadedStartDate;
    private EndDate preloadedEndDate;
    private String preloadedEventId;
    private String preloadedTimezone;
    private String currentTitle;
    private boolean notifyCreation;

    private static int TWENTY_THREE = 23;

    //region Initialization
    @Inject public NewEventPresenter(DateFormatter dateFormatter, TimeFormatter timeFormatter,
      CreateEventInteractor createEventInteractor, GetEventInteractor getEventInteractor,
      EventModelMapper eventModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
        this.createEventInteractor = createEventInteractor;
        this.getEventInteractor = getEventInteractor;
        this.eventModelMapper = eventModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(NewEventView newEventView, List<EndDate> suggestedEndDates, String optionalIdEventToEdit) {
        this.newEventView = newEventView;
        this.suggestedEndDates = suggestedEndDates;
        isNewEvent = optionalIdEventToEdit == "0L";
        if (isNewEvent) {
            this.setDefaultTimezone();
            this.setDefaultStartDateTime();
            this.setDefaultEndDateTime();
        } else {
            this.preloadEventToEdit(optionalIdEventToEdit);
        }
    }

    private void setDefaultTimezone() {
        this.setTimezone(TimeZone.getDefault());
    }

    private void preloadEventToEdit(String optionalIdEventToEdit) {
        getEventInteractor.loadEvent(optionalIdEventToEdit, new GetEventInteractor.Callback() {
            @Override public void onLoaded(Event event) {
                setDefaultEventInfo(eventModelMapper.transform(event));
            }
        });
    }

    private void setDefaultEventInfo(EventModel eventModel) {
        preloadedTimezone = eventModel.getTimezone();
        if (preloadedTimezone != null) {
            setTimezone(TimeZone.getTimeZone(preloadedTimezone));
        } else {
            setDefaultTimezone();
        }

        preloadedEventId = eventModel.getIdEvent();
        preloadedTitle = eventModel.getTitle();
        newEventView.setEventTitle(preloadedTitle);

        preloadedStartDate = fakeDateFromRealTimezone(eventModel.getStartDate());
        setStartDateTime(new MutableDateTime(preloadedStartDate));

        long offsetEndDate = fakeDateFromRealTimezone(eventModel.getEndDate());
        preloadedEndDate = endDateFromTimestamp(offsetEndDate, preloadedStartDate);
        setEndDateTime(preloadedEndDate);
    }

    private void setDefaultStartDateTime() {
        MutableDateTime currentDateTime = new MutableDateTime();
        roundDateUp(currentDateTime);
        setStartDateTime(currentDateTime);
    }

    private void roundDateUp(MutableDateTime currentDateTime) {
        if (currentDateTime.getMinuteOfHour() != 0) {
            currentDateTime.setMinuteOfHour(0);
            currentDateTime.setHourOfDay(nextAbsoluteHour(currentDateTime.getHourOfDay()));

        }
    }

    private int nextAbsoluteHour(int baseHour){
        if(baseHour == TWENTY_THREE){
            return TWENTY_THREE;
        }else{
            return baseHour + 1;
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
        currentTitle = filterTitle(title);
        this.updateDoneButtonStatus();
    }

    public void startDateSelected(int year, int month, int day) {
        selectedStartDateTime.setYear(year);
        selectedStartDateTime.setMonthOfYear(month);
        selectedStartDateTime.setDayOfMonth(day);
        setStartDateTime(selectedStartDateTime);
    }

    public void startTimeSelected(int hour, int minutes) {
        selectedStartDateTime.setHourOfDay(hour);
        selectedStartDateTime.setMinuteOfHour(minutes);
        setStartDateTime(selectedStartDateTime);
    }

    public void endDateItemSelected(int selectedItemId) {
        if (selectedItemId == R.id.end_date_custom) {
            newEventView.pickCustomDateTime(selectedEndDate.getDateTime(selectedStartDateTime.getMillis()), timezoneDisplayText());
        } else {
            EndDate endDate = endDateFromItemId(selectedItemId);
            this.setEndDateTime(endDate);
        }
    }

    public void customEndDateSelected(long selectedTimestamp) {
        FixedEndDate endDate = new FixedEndDate(selectedTimestamp, R.id.end_date_custom, timeFormatter, dateFormatter);
        this.setEndDateTime(endDate);
    }

    public void pickTimezone() {
        newEventView.navigateToPickTimezone(selectedTimeZone.getID());
    }

    public void timezoneSelected(String selectedTimezoneId) {
        TimeZone timeZone = TimeZone.getTimeZone(selectedTimezoneId);
        this.setTimezone(timeZone);
    }

    public void done() {
        newEventView.hideKeyboard();
        if (isNewEvent && isInTimeRangeForNotification()) {
            this.askNotificationConfirmation();
        } else {
            newEventView.showLoading();
            this.editEvent(preloadedEventId);
        }
    }

    private boolean isInTimeRangeForNotification() {
        long realStartDate = realDateFromFakeTimezone(selectedStartDateTime.getMillis());
        long currentDate = System.currentTimeMillis();
        long sixHoursAgo = currentDate - SIX_HOURS_MILLIS;
        long sixHoursAhead = currentDate + SIX_HOURS_MILLIS;
        boolean startsBefore6Hours = realStartDate < sixHoursAhead;
        boolean startedAfter6HoursAgo = realStartDate > sixHoursAgo;
        return startsBefore6Hours &&  startedAfter6HoursAgo;

    }

    private void askNotificationConfirmation() {
        newEventView.showNotificationConfirmation();
    }

    public void confirmNotify(boolean notify) {
        notifyCreation = notify;
        newEventView.showLoading();
        createEvent();
    }

    private void createEvent() {
        sendEvent(null);
    }

    private void editEvent(String preloadedEventId) {
        sendEvent(preloadedEventId);
    }

    private void sendEvent(String preloadedEventId) {
        long startTimestamp = realDateFromFakeTimezone(selectedStartDateTime.getMillis());
        long endTimestamp = realDateFromFakeTimezone(selectedEndDate.getDateTime(selectedStartDateTime.getMillis()));
        String title = filterTitle(newEventView.getEventTitle());
        createEventInteractor.sendEvent(preloadedEventId, title, startTimestamp, endTimestamp, selectedTimeZone.getID(), notifyCreation,
          new CreateEventInteractor.Callback() {
                @Override public void onLoaded(Event event) {
                    eventCreated(event);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    eventCreationError(error);
                }
            });
    }

    private int realTimezoneOffset(long date) {
        TimeZone deviceTimeZone = TimeZone.getDefault();
        return deviceTimeZone.getOffset(date);
    }

    private int fakeTimezoneOffset(long date) {
        return selectedTimeZone.getOffset(date);
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
        } else if (error instanceof ServerCommunicationException) {
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

    //region Date timezone offsets
    private long realDateFromFakeTimezone(long fakeDate) {
        return fakeDate + eventDateOffset(fakeDate);
    }

    private long fakeDateFromRealTimezone(long realDate) {
        return realDate - eventDateOffset(realDate);
    }

    private long eventDateOffset(long eventDate) {
        return realTimezoneOffset(eventDate) - fakeTimezoneOffset(eventDate);
    }
    //endregion

    //region Utils
    private String filterTitle(String title) {
        return title.trim();
    }

    private void setStartDateTime(MutableDateTime dateTime) {
        selectedStartDateTime = dateTime;
        this.setViewStartDateTime(dateTime);
        this.updateDoneButtonStatus();
    }

    private void setEndDateTime(EndDate endDate) {
        selectedEndDate = endDate;
        newEventView.setEndDate(endDate.getTitle());
        this.updateDoneButtonStatus();
    }

    private void setTimezone(TimeZone timeZone) {
        this.selectedTimeZone = timeZone;
        this.updateViewTimezone();
        this.updateDoneButtonStatus();
    }

    private void updateViewTimezone() {
        newEventView.setTimeZone(timezoneDisplayText());
    }

    private String timezoneDisplayText() {
        long timezoneTime;
        if (selectedStartDateTime != null) {
            timezoneTime = selectedStartDateTime.getMillis();
        } else {
            timezoneTime = System.currentTimeMillis();
        }
        return selectedTimeZone.getID() +" "+ dateFormatter.getGMT(selectedTimeZone, timezoneTime);
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

    private void updateDoneButtonStatus() {
        newEventView.doneButtonEnabled(canSendEvent());
    }

    private boolean canSendEvent() {
        return isValidTitle() && (hasChangedTitle()
          || hasChangedStartDate()
          || hasChangedEndDate()
          || hasChangedTimezone());
    }

    private boolean hasChangedTimezone() {
        return !selectedTimeZone.getID().equals(preloadedTimezone);
    }

    private boolean isValidTitle() {
        return currentTitle != null && currentTitle.length() >= MINIMUM_TITLE_LENGTH;
    }

    private boolean hasChangedStartDate() {
        return selectedStartDateTime != null && selectedStartDateTime.getMillis() != preloadedStartDate;
    }

    private boolean hasChangedEndDate() {
        return preloadedEndDate == null
          || selectedEndDate.getDateTime(selectedStartDateTime.getMillis()) != preloadedEndDate.getDateTime(
          preloadedStartDate);
    }

    private boolean hasChangedTitle() {
        return !newEventView.getEventTitle().equals(preloadedTitle);
    }
    //endregion

    @Override public void resume() {

    }

    @Override public void pause() {

    }
}
