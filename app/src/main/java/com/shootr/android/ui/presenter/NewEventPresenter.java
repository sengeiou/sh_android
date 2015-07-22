package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.CreateStreamInteractor;
import com.shootr.android.domain.interactor.event.DeleteStreamInteractor;
import com.shootr.android.domain.interactor.event.GetStreamInteractor;
import com.shootr.android.domain.validation.StreamValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.views.NewEventView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class NewEventPresenter implements Presenter {

    public static final int MINIMUM_TITLE_LENGTH = 3;
    public static final int MINIMUM_SHORT_TITLE_LENGTH = 3;
    public static final int MAX_SHORT_TITLE_LENGTH = 20;

    private final CreateStreamInteractor createStreamInteractor;
    private final GetStreamInteractor getStreamInteractor;
    private final DeleteStreamInteractor deleteStreamInteractor;
    private final StreamModelMapper streamModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private NewEventView newEventView;

    private boolean isNewEvent;
    private String preloadedTitle;
    private String preloadedShortTitle;
    private String preloadedEventId;
    private String currentTitle;
    private String currentShortTitle;
    private boolean notifyCreation;
    private boolean shortTitleEditedManually;

    //region Initialization
    @Inject public NewEventPresenter(CreateStreamInteractor createStreamInteractor,
      GetStreamInteractor getStreamInteractor,
      DeleteStreamInteractor deleteStreamInteractor,
      StreamModelMapper streamModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.createStreamInteractor = createStreamInteractor;
        this.getStreamInteractor = getStreamInteractor;
        this.deleteStreamInteractor = deleteStreamInteractor;
        this.streamModelMapper = streamModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(NewEventView newEventView, String optionalIdEventToEdit) {
        this.newEventView = newEventView;
        this.isNewEvent = optionalIdEventToEdit == null;
        this.shortTitleEditedManually = false;
        if (!isNewEvent) {
            this.preloadEventToEdit(optionalIdEventToEdit);
        }
        updateDoneButtonStatus();
    }

    private void preloadEventToEdit(String optionalIdEventToEdit) {
        getStreamInteractor.loadStream(optionalIdEventToEdit, new GetStreamInteractor.Callback() {
            @Override public void onLoaded(Stream stream) {
                setDefaultEventInfo(streamModelMapper.transform(stream));
            }
        });
    }

    private void setDefaultEventInfo(StreamModel streamModel) {
        preloadedEventId = streamModel.getIdStream();
        preloadedTitle = streamModel.getTitle();
        newEventView.setEventTitle(preloadedTitle);
        newEventView.showDeleteEventButton();
        if (currentTitle == null && currentShortTitle == null) {
            preloadedTitle = streamModel.getTitle();
            preloadedShortTitle = streamModel.getTag();
            currentTitle = preloadedTitle;
            currentShortTitle = preloadedShortTitle;
            newEventView.setEventTitle(preloadedTitle);
            newEventView.showShortTitle(preloadedShortTitle);

            bindShortTitleToTitleIfMatches();
        }
    }
    //endregion

    //region Interaction methods
    public void titleTextChanged(String title) {
        currentTitle = filterTitle(title);
        if(!shortTitleEditedManually){
            currentShortTitle = filterShortTitle(title);
            newEventView.showShortTitle(currentShortTitle);
        }
        this.updateDoneButtonStatus();
    }

    public void shortTitleTextChanged(String shortTitle) {
        currentShortTitle = filterShortTitle(shortTitle);
        this.bindShortTitleToTitleIfMatches();
        this.updateDoneButtonStatus();
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

    public void delete() {
        newEventView.askDeleteEventConfirmation();
    }

    public void confirmDeleteEvent() {
        deleteStreamInteractor.deleteStream(preloadedEventId, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                newEventView.closeScreenWithExitEvent();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                String errorMessage = errorMessageFactory.getMessageForError(error);
                newEventView.showError(errorMessage);
            }
        });
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
        createStreamInteractor.sendStream(preloadedEventId,
          title,
          shortTitle,
          notifyCreation,
          new CreateStreamInteractor.Callback() {
              @Override public void onLoaded(Stream stream) {
                  eventCreated(stream);
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  eventCreationError(error);
              }
          });
    }

    private void eventCreated(Stream stream) {
        newEventView.closeScreenWithResult(stream.getId(), stream.getTitle());
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
            Timber.e(error, "Unhandled error creating stream.");
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
                case StreamValidator.FIELD_TITLE:
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
        if(shortTitle.length() <= 20){
            return shortTitle.trim();
        }else {
            return shortTitle.substring(0,20).trim();
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

    private void bindShortTitleToTitleIfMatches() {
        if (currentTitle != null && currentShortTitle != null) {
            shortTitleEditedManually = !filterShortTitle(currentTitle).equals(currentShortTitle);
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
