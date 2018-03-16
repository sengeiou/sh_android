package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.InvalidYoutubeVideoUrlException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.mobile.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UpdateStreamInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.validation.FieldValidationError;
import com.shootr.mobile.domain.validation.StreamValidator;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.views.NewStreamView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class NewStreamPresenter implements Presenter {

  public static final int MINIMUM_TITLE_LENGTH = 3;

  private final CreateStreamInteractor createStreamInteractor;
  private final UpdateStreamInteractor updateStreamInteractor;
  private final GetStreamInteractor getStreamInteractor;
  private final ChangeStreamPhotoInteractor changeStreamPhotoInteractor;
  private final SelectStreamInteractor selectStreamInteractor;
  private final StreamModelMapper streamModelMapper;
  private final ErrorMessageFactory errorMessageFactory;
  private final SessionRepository sessionRepository;

  private NewStreamView newStreamView;
  private boolean isNewStream;
  private String preloadedStreamId;
  private String currentTitle;
  private boolean notifyCreation;
  private String currentStreamTopic;
  private String newIdMedia;
  private StreamModel streamModel;
  private boolean removePhoto;

  //region Initialization
  @Inject public NewStreamPresenter(CreateStreamInteractor createStreamInteractor,
      UpdateStreamInteractor updateStreamInteractor, GetStreamInteractor getStreamInteractor,
      ChangeStreamPhotoInteractor changeStreamPhotoInteractor,
      SelectStreamInteractor selectStreamInteractor, StreamModelMapper streamModelMapper,
      ErrorMessageFactory errorMessageFactory, SessionRepository sessionRepository) {
    this.createStreamInteractor = createStreamInteractor;
    this.updateStreamInteractor = updateStreamInteractor;
    this.getStreamInteractor = getStreamInteractor;
    this.changeStreamPhotoInteractor = changeStreamPhotoInteractor;
    this.selectStreamInteractor = selectStreamInteractor;
    this.streamModelMapper = streamModelMapper;
    this.errorMessageFactory = errorMessageFactory;
    this.sessionRepository = sessionRepository;
  }

  public void initialize(NewStreamView newStreamView, String optionalIdStreamToEdit) {
    this.newStreamView = newStreamView;
    this.isNewStream = optionalIdStreamToEdit == null;
    if (!isNewStream) {
      this.preloadStreamToEdit(optionalIdStreamToEdit);
    } else {
      newStreamView.showEditPhotoPlaceHolder();
    }
    updateDoneButtonStatus();
    hideVideoUrlFields();
  }

  private void hideVideoUrlFields() {
    if (!sessionRepository.isNewTimeline()) {
      newStreamView.hideVideoUrlFields();
    }
  }

  private void preloadStreamToEdit(String optionalIdStreamToEdit) {
    getStreamInteractor.loadStream(optionalIdStreamToEdit, new GetStreamInteractor.Callback() {
      @Override public void onLoaded(Stream stream) {
        setDefaultStreamInfo(streamModelMapper.transform(stream));
      }
    });
  }

  private void setDefaultStreamInfo(StreamModel streamModel) {
    this.streamModel = streamModel;
    preloadedStreamId = streamModel.getIdStream();
    String preloadedTitle = streamModel.getTitle();
    newStreamView.setStreamTitle(preloadedTitle);
    newStreamView.showDescription(streamModel.getDescription());
    newStreamView.showVideoUrl(streamModel.getVideoUrl());
    newStreamView.setModeValue(streamModel.getReadWriteMode());
    if (streamModel.getPicture() != null) {
      newStreamView.setStreamPhoto(streamModel.getPicture(), streamModel.getTitle());
    } else {
      newStreamView.showEditPhotoPlaceHolder();
    }
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

  public void done(String streamTitle, String streamDescription, Integer streamMode,
      String videoUrl) {
    newStreamView.hideKeyboard();
    if (isNewStream) {
      this.askNotificationConfirmation();
    } else {
      newStreamView.showLoading();
      editStream(preloadedStreamId, streamTitle, streamDescription, streamMode, videoUrl);
    }
  }

  private void askNotificationConfirmation() {
    newStreamView.showNotificationConfirmation();
  }

  public void confirmNotify(String streamTitle, String streamDescription, Integer streamMode,
      boolean notify, String videoUrl) {
    notifyCreation = notify;
    newStreamView.showLoading();
    createStream(streamTitle, streamDescription, streamMode, videoUrl);
  }

  private void createStream(String streamTitle, String streamDescription, Integer streamMode,
      String videoUrl) {
    sendStream(streamTitle, streamDescription, streamMode, videoUrl);
  }

  private void editStream(String preloadedStreamId, String streamTitle, String streamDescription,
      Integer streamMode, String videoUrl) {
    updateStream(preloadedStreamId, streamTitle, streamDescription, streamMode, videoUrl);
  }

  private void updateStream(String preloadedStreamId, String streamTitle, String streamDescription,
      Integer streamMode, String videoUrl) {
    updateStreamInteractor.updateStream(preloadedStreamId, streamTitle, streamDescription,
        streamMode, newIdMedia, videoUrl, new UpdateStreamInteractor.Callback() {
          @Override public void onLoaded(Stream stream) {
            streamCreated(stream);
            selectStream(stream);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            streamCreationError(error);
          }
        });
  }

  private void sendStream(String streamTitle, String streamDescription, Integer streamMode,
      String videoUrl) {

    createStreamInteractor.sendStream(streamTitle, streamDescription, streamMode, newIdMedia,
        videoUrl, notifyCreation, new CreateStreamInteractor.Callback() {
          @Override public void onLoaded(Stream stream) {
            streamCreated(stream);
            selectStream(stream);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            streamCreationError(error);
          }
        });
  }

  protected void selectStream(Stream stream) {
    selectStreamInteractor.selectStream(stream.getId(),
        new Interactor.Callback<StreamSearchResult>() {
          @Override public void onLoaded(StreamSearchResult streamSearchResult) {
                /* no-op */
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
                /* no-op */
          }
        });
  }

  private void streamCreated(Stream stream) {
    if (!isNewStream) {
      newStreamView.closeScreenWithResult(stream.getId());
    } else {
      newStreamView.goToShareStream(stream.getId());
    }
  }

  private void streamCreationError(ShootrException error) {
    newStreamView.hideLoading();
    if (error instanceof InvalidYoutubeVideoUrlException) {
      showViewVideoUrlError(error.getMessage());
    } else if (error instanceof DomainValidationException) {
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

  private void showViewVideoUrlError(String errorMessage) {
    newStreamView.showVideoUrlError();
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

  public void photoSelected(final File photoFile) {
    changeStreamPhotoInteractor.changeStreamPhoto(null, photoFile,
        new ChangeStreamPhotoInteractor.Callback() {
          @Override public void onLoaded(String idMedia) {
            newIdMedia = idMedia;
            newStreamView.showPhotoSelected(photoFile);
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            Timber.e(error, "Error changing stream photo");
          }
        });
  }

  //endregion

  @Override public void resume() {
        /* no-op */
  }

  @Override public void pause() {
        /* no-op */
  }

  public void photoClick() {
    if (!isNewStream && streamModel.getPicture() != null) {
      newStreamView.showPhotoOptions();
    } else {
      newStreamView.showPhotoPicker();
    }
  }

  public void viewPhotoClicked() {
    zoomPhoto();
  }

  public void zoomPhoto() {
    newStreamView.zoomPhoto(streamModel.getPicture());
  }

  public void removePhoto() {
    newIdMedia = "";
    newStreamView.showEditPhotoPlaceHolder();
  }
}
