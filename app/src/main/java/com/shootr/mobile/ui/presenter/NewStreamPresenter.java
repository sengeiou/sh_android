package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.validation.FieldValidationError;
import com.shootr.mobile.domain.validation.StreamValidator;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.views.NewStreamView;
import com.shootr.mobile.util.ErrorMessageFactory;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class NewStreamPresenter implements Presenter {

    public static final int MINIMUM_TITLE_LENGTH = 3;

    private final CreateStreamInteractor createStreamInteractor;
    private final GetStreamInteractor getStreamInteractor;
    private final SelectStreamInteractor selectStreamInteractor;
    private final StreamModelMapper streamModelMapper;
    private final ErrorMessageFactory errorMessageFactory;

    private NewStreamView newStreamView;

    private boolean isNewStream;
    private String preloadedStreamId;
    private String currentTitle;
    private boolean notifyCreation;
    private String currentStreamTopic;

    //region Initialization
    @Inject
    public NewStreamPresenter(CreateStreamInteractor createStreamInteractor,
                              GetStreamInteractor getStreamInteractor, SelectStreamInteractor selectStreamInteractor,
                              StreamModelMapper streamModelMapper, ErrorMessageFactory errorMessageFactory) {
        this.createStreamInteractor = createStreamInteractor;
        this.getStreamInteractor = getStreamInteractor;
        this.selectStreamInteractor = selectStreamInteractor;
        this.streamModelMapper = streamModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void initialize(NewStreamView newStreamView, String optionalIdStreamToEdit) {
        this.newStreamView = newStreamView;
        this.isNewStream = optionalIdStreamToEdit == null;
        if (!isNewStream) {
            this.preloadStreamToEdit(optionalIdStreamToEdit);
        }
        updateDoneButtonStatus();
    }

    private void preloadStreamToEdit(String optionalIdStreamToEdit) {
        getStreamInteractor.loadStream(optionalIdStreamToEdit, new GetStreamInteractor.Callback() {
            @Override
            public void onLoaded(Stream stream) {
                setDefaultStreamInfo(streamModelMapper.transform(stream));
            }
        });
    }

    private void setDefaultStreamInfo(StreamModel streamModel) {
        preloadedStreamId = streamModel.getIdStream();
        String preloadedTitle = streamModel.getTitle();
        newStreamView.setStreamTitle(preloadedTitle);
        newStreamView.showDescription(streamModel.getDescription());
        newStreamView.setModeValue(streamModel.getReadWriteMode());
        if (currentTitle == null) {
            preloadedTitle = streamModel.getTitle();
            currentTitle = preloadedTitle;
            newStreamView.setStreamTitle(preloadedTitle);
        }
        this.currentStreamTopic = streamModel.getTopic();
    }
    //endregion

    //region Interaction methods
    public void titleTextChanged(String title) {
        currentTitle = filterTitle(title);
        this.updateDoneButtonStatus();
    }

    public void done(String streamTitle, String streamDescription, Integer streamMode) {
        newStreamView.hideKeyboard();
        if (isNewStream) {
            this.askNotificationConfirmation();
        } else {
            newStreamView.showLoading();
            editStream(preloadedStreamId, streamTitle, streamDescription, streamMode);
        }
    }

    private void askNotificationConfirmation() {
        newStreamView.showNotificationConfirmation();
    }

    public void confirmNotify(String streamTitle, String streamDescription, Integer streamMode, boolean notify) {
        notifyCreation = notify;
        newStreamView.showLoading();
        createStream(streamTitle, streamDescription, streamMode);
    }

    private void createStream(String streamTitle, String streamDescription, Integer streamMode) {
        sendStream(null, streamTitle, streamDescription, streamMode);
    }

    private void editStream(String preloadedStreamId, String streamTitle, String streamDescription,
                            Integer streamMode) {
        sendStream(preloadedStreamId, streamTitle, streamDescription, streamMode);
    }

    private void sendStream(String preloadedStreamId, String streamTitle, String streamDescription,
                            Integer streamMode) {
        createStreamInteractor.sendStream(preloadedStreamId,
                streamTitle,
                streamDescription,
                streamMode,
                currentStreamTopic,
                notifyCreation,
                false,
                new CreateStreamInteractor.Callback() {
                    @Override
                    public void onLoaded(Stream stream) {
                        streamCreated(stream);
                        seletStream(stream);
                    }
                },
                new Interactor.ErrorCallback() {
                    @Override
                    public void onError(ShootrException error) {
                        streamCreationError(error);
                    }
                });
    }

    protected void seletStream(Stream stream) {
        selectStreamInteractor.selectStream(stream.getId(), new Interactor.Callback<StreamSearchResult>() {
            @Override
            public void onLoaded(StreamSearchResult streamSearchResult) {
                /* no-op */
            }
        }, new Interactor.ErrorCallback() {
            @Override
            public void onError(ShootrException error) {
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

    private String filterDescription(String streamDescription) {
        return streamDescription.trim();
    }

    private void updateDoneButtonStatus() {
        newStreamView.doneButtonEnabled(canSendStream());
    }

    private boolean canSendStream() {
        return isValidTitle();
    }

    private boolean isValidTitle() {
        return currentTitle != null && currentTitle.length() >= MINIMUM_TITLE_LENGTH;
    }

    //endregion

    @Override
    public void resume() {
        /* no-op */
    }

    @Override
    public void pause() {
        /* no-op */
    }
}
