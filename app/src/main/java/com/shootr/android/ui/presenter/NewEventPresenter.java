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
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class NewEventPresenter implements Presenter {

    public static final int MINIMUM_TITLE_LENGTH = 3;
    public static final int MINIMUM_SHORT_TITLE_LENGTH = 3;
    public static final int MAX_SHORT_TITLE_LENGTH = 15;

    private final CreateEventInteractor createEventInteractor;
    private final GetEventInteractor getEventInteractor;
    private final EventModelMapper eventModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private NewEventView newEventView;

    private boolean isNewEvent;
    private String preloadedTitle;
    private String preloadedShortTitle;
    private String preloadedEventId;
    private String currentTitle;
    private String currentShortTitle;
    private boolean notifyCreation;

    //region Initialization
    @Inject public NewEventPresenter(CreateEventInteractor createEventInteractor, GetEventInteractor getEventInteractor,
      EventModelMapper eventModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.createEventInteractor = createEventInteractor;
        this.getEventInteractor = getEventInteractor;
        this.eventModelMapper = eventModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(NewEventView newEventView, String optionalIdEventToEdit) {
        this.newEventView = newEventView;
        this.isNewEvent = optionalIdEventToEdit == null;
        if (!isNewEvent) {
            this.preloadEventToEdit(optionalIdEventToEdit);
        }
    }

    private void preloadEventToEdit(String optionalIdEventToEdit) {
        getEventInteractor.loadEvent(optionalIdEventToEdit, new GetEventInteractor.Callback() {
            @Override public void onLoaded(Event event) {
                setDefaultEventInfo(eventModelMapper.transform(event));
            }
        });
    }

    private void setDefaultEventInfo(EventModel eventModel) {
        preloadedEventId = eventModel.getIdEvent();
        preloadedTitle = eventModel.getTitle();
        preloadedShortTitle = eventModel.getTag();
        currentShortTitle = preloadedShortTitle;
        newEventView.setEventTitle(preloadedTitle);
        newEventView.showShortTitle(preloadedShortTitle);
    }
    //endregion

    //region Interaction methods
    public void titleTextChanged(String title) {
        currentTitle = filterTitle(title);
        currentShortTitle = filterShortTitle(title);
        this.updateDoneButtonStatus();
        newEventView.showShortTitle(currentShortTitle);
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
        String shortTitle = filterShortTitle(newEventView.getEventShortTitle());
        createEventInteractor.sendEvent(preloadedEventId, title, shortTitle, notifyCreation, new CreateEventInteractor.Callback() {
            @Override public void onLoaded(Event event) {
                eventCreated(event);
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                eventCreationError(error);
            }
        });
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

    //region Utils
    private String filterTitle(String title) {
        return title.trim();
    }

    private String filterShortTitle(String shortTitle) {
        if(shortTitle.length() < 15){
            return shortTitle.trim();
        }else {
            return shortTitle.substring(0,14).trim();
        }
    }

    private void updateDoneButtonStatus() {
        newEventView.doneButtonEnabled(canSendEvent());
    }

    private boolean canSendEvent() {
        return isValidTitle() && isValidShortTitle();
    }

    private boolean isValidTitle() {
        return currentTitle != null && currentTitle.length() >= MINIMUM_TITLE_LENGTH;
    }

    private boolean isValidShortTitle() {
        return currentShortTitle != null && currentShortTitle.length() >= MINIMUM_SHORT_TITLE_LENGTH
          && currentShortTitle.length() <= MAX_SHORT_TITLE_LENGTH;
    }

    public void shortTitleTextChanged(String shortTitle) {
        updateShortTitleWarning(shortTitle);
        updateShortTitle(shortTitle);
        this.updateDoneButtonStatus();
    }

    private void updateShortTitle(String shortTitle) {
        if(shortTitle.length() < 15) {
            currentShortTitle = filterShortTitle(shortTitle);
        } else {
            newEventView.showShortTitle(currentShortTitle);
        }
    }

    private void updateShortTitleWarning(String shortTitle) {
        if(currentShortTitle != null && shortTitle.length() < currentShortTitle.length()) {
            newEventView.showShortTitleWarning();
        } else {
            newEventView.hideShortTitleWarning();
        }
    }

    //endregion

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
