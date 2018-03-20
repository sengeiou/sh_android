package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.InvalidYoutubeVideoUrlException;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrServerException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamUpdateParameters;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.validation.FieldValidationError;
import com.shootr.mobile.domain.validation.StreamValidator;
import java.util.List;
import javax.inject.Inject;

public class UpdateStreamInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalStreamRepository remoteStreamRepository;

  private String idStream;
  private String title;
  private String description;
  private String topic;
  private String idMedia;
  private String videoUrl;
  private Callback callback;
  private ErrorCallback errorCallback;

  private boolean notifyTopicMessage;
  private String streamMode;

  private boolean isEditingTopic = false;

  @Inject public UpdateStreamInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalStreamRepository remoteStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteStreamRepository = remoteStreamRepository;
  }

  public void updateStream(String idStream, String title, String description, Integer streamMode,
      String idMedia, String videoUrl, Callback callback, ErrorCallback errorCallback) {
    this.idStream = idStream;
    this.title = title;
    this.description = description;
    this.streamMode = getStreamMode(streamMode);
    this.idMedia = idMedia;
    this.videoUrl = videoUrl;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  public void updateStreamMessage(String idStream, String topic, Boolean notifyTopicMessage,
      String videoUrl, Callback callback, ErrorCallback errorCallback) {
    isEditingTopic = true;
    this.idStream = idStream;
    this.topic = topic == null ? "" : topic;
    this.videoUrl = videoUrl;
    this.notifyTopicMessage = notifyTopicMessage;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    StreamUpdateParameters streamUpdateParameters = streamFromParameters();

    if (isEditingTopic) {
      sendAndNotify(streamUpdateParameters);
    } else {
      if (validateStream(streamUpdateParameters)) {
        sendAndNotify(streamUpdateParameters);
      }
    }
  }

  private void sendAndNotify(StreamUpdateParameters streamUpdateParameters) {
    try {
      Stream savedStream = sendStreamToServer(streamUpdateParameters);
      notifyLoaded(savedStream);
    } catch (InvalidYoutubeVideoUrlException err) {
      notifyError(err);
    } catch (ShootrException e) {
      handleServerError(e);
    }
  }

  private String getStreamMode(Integer streamMode) {
    return streamMode == 0 ? StreamMode.PUBLIC : StreamMode.VIEW_ONLY;
  }

  private StreamUpdateParameters streamFromParameters() {
    StreamUpdateParameters streamUpdateParameters = new StreamUpdateParameters();

    streamUpdateParameters.setIdStream(idStream);
    streamUpdateParameters.setPhotoIdMedia(idMedia);
    streamUpdateParameters.setTitle(title);
    streamUpdateParameters.setDescription(removeDescriptionLineBreaks(description));
    streamUpdateParameters.setPhotoIdMedia(idMedia);
    streamUpdateParameters.setReadWriteMode(streamMode);
    streamUpdateParameters.setTopic(topic);
    streamUpdateParameters.setVideoUrl(videoUrl);
    streamUpdateParameters.setNotifyMessage(notifyTopicMessage);

    return streamUpdateParameters;
  }

  private Stream sendStreamToServer(StreamUpdateParameters streamUpdateParameters) {
    return remoteStreamRepository.updateStream(streamUpdateParameters);
  }

  //region Validation
  private boolean validateStream(StreamUpdateParameters stream) {
    List<FieldValidationError> validationErrors = new StreamValidator().validate(stream);
    if (validationErrors.isEmpty()) {
      return true;
    } else {
      notifyError(new DomainValidationException(validationErrors));
      return false;
    }
  }

  private String removeDescriptionLineBreaks(String description) {
    if (description == null || description.isEmpty()) {
      return "";
    } else {
      return description.replace("\n", "").replace("\r", "");
    }
  }

  private void handleServerError(ShootrException e) {
    if (e.getCause() instanceof ShootrServerException) {
      ShootrServerException serverException = (ShootrServerException) e.getCause();
      String errorCode = serverException.getShootrError().getErrorCode();
      FieldValidationError validationError = validationErrorFromCode(errorCode);
      if (validationError != null) {
        notifyError(new DomainValidationException(validationError));
        return;
      }
    }
    notifyError(e);
  }

  private FieldValidationError validationErrorFromCode(String errorCode) {
    int field = fieldFromErrorCode(errorCode);
    if (field != 0) {
      return new FieldValidationError(errorCode, field);
    } else {
      return null;
    }
  }

  private int fieldFromErrorCode(String errorCode) {
    switch (errorCode) {
      case ShootrError.ERROR_CODE_STREAM_TITLE_TOO_SHORT:
      case ShootrError.ERROR_CODE_STREAM_TITLE_TOO_LONG:
        return StreamValidator.FIELD_TITLE;
      default:
    }
    return 0;
  }
  //endregion

  private void notifyLoaded(final Stream stream) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(stream);
      }
    });
  }

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }

  public interface Callback {

    void onLoaded(Stream stream);
  }
}
