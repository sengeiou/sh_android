package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.android.domain.interactor.stream.GetStreamInteractor;
import com.shootr.android.domain.interactor.stream.RemoveStreamInteractor;
import com.shootr.android.domain.interactor.stream.RestoreStreamInteractor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.domain.validation.StreamValidator;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.views.NewStreamView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class NewStreamPresenter implements Presenter {

    public static final int MINIMUM_TITLE_LENGTH = 3;
    public static final int MINIMUM_SHORT_TITLE_LENGTH = 3;
    public static final int MAX_SHORT_TITLE_LENGTH = 20;

    private final CreateStreamInteractor createStreamInteractor;
    private final GetStreamInteractor getStreamInteractor;
    private final RemoveStreamInteractor removeStreamInteractor;
    private final RestoreStreamInteractor restoreStreamInteractor;
    private final SelectStreamInteractor selectStreamInteractor;
    private final StreamModelMapper streamModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private NewStreamView newStreamView;

    private boolean isNewStream;
    private String preloadedStreamId;
    private String currentTitle;
    private String currentShortTitle;
    private boolean notifyCreation;
    private boolean shortTitleEditedManually;

    //region Initialization
    @Inject public NewStreamPresenter(CreateStreamInteractor createStreamInteractor,
      GetStreamInteractor getStreamInteractor, RemoveStreamInteractor removeStreamInteractor,
      RestoreStreamInteractor restoreStreamInteractor, SelectStreamInteractor selectStreamInteractor, StreamModelMapper streamModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.createStreamInteractor = createStreamInteractor;
        this.getStreamInteractor = getStreamInteractor;
        this.removeStreamInteractor = removeStreamInteractor;
        this.restoreStreamInteractor = restoreStreamInteractor;
        this.selectStreamInteractor = selectStreamInteractor;
        this.streamModelMapper = streamModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(NewStreamView newStreamView, String optionalIdStreamToEdit) {
        this.newStreamView = newStreamView;
        this.isNewStream = optionalIdStreamToEdit == null;
        this.shortTitleEditedManually = false;
        if (!isNewStream) {
            this.preloadStreamToEdit(optionalIdStreamToEdit);
        }
        updateDoneButtonStatus();
    }

    private void preloadStreamToEdit(String optionalIdStreamToEdit) {
        getStreamInteractor.loadStream(optionalIdStreamToEdit, new GetStreamInteractor.Callback() {
            @Override public void onLoaded(Stream stream) {
                setDefaultStreamInfo(streamModelMapper.transform(stream));
            }
        });
    }

    private void setDefaultStreamInfo(StreamModel streamModel) {
        preloadedStreamId = streamModel.getIdStream();
        String preloadedTitle = streamModel.getTitle();
        newStreamView.setStreamTitle(preloadedTitle);
        newStreamView.showShortTitle(streamModel.getShortTitle());
        newStreamView.showDescription(streamModel.getDescription());

        if (streamModel.isRemoved()) {
            newStreamView.showRestoreStreamButton();
        } else {
            newStreamView.showRemoveStreamButton();
        }
        if (currentTitle == null && currentShortTitle == null) {
            preloadedTitle = streamModel.getTitle();
            String preloadedShortTitle = streamModel.getShortTitle();
            currentTitle = preloadedTitle;
            currentShortTitle = preloadedShortTitle;
            newStreamView.setStreamTitle(preloadedTitle);
            bindShortTitleToTitleIfMatches();
        }
    }
    //endregion

    //region Interaction methods
    public void titleTextChanged(String title) {
        currentTitle = filterTitle(title);
        if(!shortTitleEditedManually){
            currentShortTitle = filterShortTitle(title);
            newStreamView.showShortTitle(currentShortTitle);
        }
        this.updateDoneButtonStatus();
    }

    public void shortTitleTextChanged(String shortTitle) {
        currentShortTitle = filterShortTitle(shortTitle);
        this.bindShortTitleToTitleIfMatches();
        this.updateDoneButtonStatus();
    }

    public void done() {
        newStreamView.hideKeyboard();
        if (isNewStream) {
            this.askNotificationConfirmation();
        } else {
            newStreamView.showLoading();
            this.editStream(preloadedStreamId);
        }
    }

    private void askNotificationConfirmation() {
        newStreamView.showNotificationConfirmation();
    }

    public void confirmNotify(boolean notify) {
        notifyCreation = notify;
        newStreamView.showLoading();
        createStream();
    }

    public void remove() {
        newStreamView.askRemoveStreamConfirmation();
    }

    public void restore() {
        restoreStreamInteractor.restoreStream(preloadedStreamId, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                newStreamView.closeScreenWithExitStream();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                String errorMessage = errorMessageFactory.getMessageForError(error);
                newStreamView.showError(errorMessage);
            }
        });
    }

    public void confirmRemoveStream() {
        removeStreamInteractor.removeStream(preloadedStreamId, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                newStreamView.closeScreenWithExitStream();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                String errorMessage = errorMessageFactory.getMessageForError(error);
                newStreamView.showError(errorMessage);
            }
        });
    }

    private void createStream() {
        sendStream(null);
    }

    private void editStream(String preloadedStreamId) {
        sendStream(preloadedStreamId);
    }

    private void sendStream(String preloadedStreamId) {
        String title = filterTitle(newStreamView.getStreamTitle());
        String shortTitle = filterShortTitle(newStreamView.getStreamShortTitle());
        String description = filterDescription(newStreamView.getStreamDescription());

        createStreamInteractor.sendStream(preloadedStreamId,
          title,
          shortTitle,
          description,
          notifyCreation,
          new CreateStreamInteractor.Callback() {
              @Override public void onLoaded(Stream stream) {
                  streamCreated(stream);
                  seletStream(stream);
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  streamCreationError(error);
              }
          });
    }

    protected void seletStream(Stream stream) {
        selectStreamInteractor.selectStream(stream.getId(), new Interactor.Callback<StreamSearchResult>() {
            @Override public void onLoaded(StreamSearchResult streamSearchResult) {
                /* no-op */
            }
        });
    }

    private void streamCreated(Stream stream) {
        newStreamView.closeScreenWithResult(stream.getId());
    }

    private void streamCreationError(ShootrException error) {
        newStreamView.hideLoading();
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
        newStreamView.showTitleError(errorMessage);
    }
    private void showViewError(String errorMessage) {
        newStreamView.showError(errorMessage);
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

    private String filterDescription(String streamDescription) {
        return streamDescription.trim();
    }

    private void updateDoneButtonStatus() {
        newStreamView.doneButtonEnabled(canSendStream());
    }

    private boolean canSendStream() {
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
