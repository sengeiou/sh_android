package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.CreateEventInteractor;
import com.shootr.android.domain.interactor.event.GetEventInteractor;
import com.shootr.android.domain.validation.EventValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.views.NewEventView;
import com.shootr.android.util.DateFormatter;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import java.util.TimeZone;
import javax.inject.Inject;
import timber.log.Timber;

public class NewEventPresenter implements Presenter {

    public static final int MINIMUM_TITLE_LENGTH = 3;

    private final DateFormatter dateFormatter;
    private final CreateEventInteractor createEventInteractor;
    private final GetEventInteractor getEventInteractor;
    private final EventModelMapper eventModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private NewEventView newEventView;

    private boolean isNewEvent;
    private TimeZone selectedTimeZone;
    private String preloadedTitle;
    private String preloadedEventId;
    private String preloadedTimezone;
    private String currentTitle;
    private boolean notifyCreation;

    //region Initialization
    @Inject public NewEventPresenter(DateFormatter dateFormatter,
      CreateEventInteractor createEventInteractor, GetEventInteractor getEventInteractor,
      EventModelMapper eventModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.dateFormatter = dateFormatter;
        this.createEventInteractor = createEventInteractor;
        this.getEventInteractor = getEventInteractor;
        this.eventModelMapper = eventModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(NewEventView newEventView, String optionalIdEventToEdit) {
        this.newEventView = newEventView;
        this.isNewEvent = optionalIdEventToEdit == null;
        if (isNewEvent) {
            this.setDefaultTimezone();
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
    }
    //endregion

    //region Interaction methods
    public void titleTextChanged(String title) {
        currentTitle = filterTitle(title);
        this.updateDoneButtonStatus();
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
        if (isNewEvent) {
            this.askNotificationConfirmation();
        } else {
            newEventView.showLoading();
            this.editEvent(preloadedEventId);
        }
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
        String title = filterTitle(newEventView.getEventTitle());
        createEventInteractor.sendEvent(preloadedEventId,
          title,
          selectedTimeZone.getID(),
          notifyCreation,
          new CreateEventInteractor.Callback() {
              @Override public void onLoaded(Event event) {
                  eventCreated(event);
              }
          },
          new Interactor.ErrorCallback() {
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
        newEventView.closeScreenWithResult(event.getId(), event.getTitle());
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
                default:
                    showViewError(errorMessage);
            }
        }
    }

    private void showViewTitleError(String errorMessage) {
        newEventView.showTitleError(errorMessage);
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
        timezoneTime = System.currentTimeMillis();
        return selectedTimeZone.getID() +" "+ dateFormatter.getGMT(selectedTimeZone, timezoneTime);
    }

    private void updateDoneButtonStatus() {
        newEventView.doneButtonEnabled(canSendEvent());
    }

    private boolean canSendEvent() {
        return isValidTitle() && (hasChangedTitle()
          || hasChangedTimezone());
    }

    private boolean hasChangedTimezone() {
        return !selectedTimeZone.getID().equals(preloadedTimezone);
    }

    private boolean isValidTitle() {
        return currentTitle != null && currentTitle.length() >= MINIMUM_TITLE_LENGTH;
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
