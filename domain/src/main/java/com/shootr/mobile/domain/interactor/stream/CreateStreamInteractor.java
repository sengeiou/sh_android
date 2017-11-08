package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrServerException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.domain.validation.FieldValidationError;
import com.shootr.mobile.domain.validation.StreamValidator;
import java.util.List;
import javax.inject.Inject;

public class CreateStreamInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final SessionRepository sessionRepository;
  private final ExternalStreamRepository remoteStreamRepository;
  private final LocaleProvider localeProvider;
  private final UserRepository localUserRepository;

  private String title;
  private String description;
  private String idMedia;
  private boolean notifyCreation;
  private Callback callback;
  private ErrorCallback errorCallback;

  private String streamMode;

  @Inject public CreateStreamInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
      ExternalStreamRepository remoteStreamRepository,
      LocaleProvider localeProvider,
      @Local UserRepository localUserRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.sessionRepository = sessionRepository;
    this.remoteStreamRepository = remoteStreamRepository;
    this.localeProvider = localeProvider;
    this.localUserRepository = localUserRepository;
  }

  public void sendStream(String title, String description, Integer streamMode,
      String idMedia, boolean notifyCreation, Callback callback,
      ErrorCallback errorCallback) {
    this.title = title;
    this.description = description;
    this.streamMode = getStreamMode(streamMode);
    this.idMedia = idMedia;
    this.notifyCreation = notifyCreation;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    Stream stream = streamFromParameters();
    if (validateStream(stream)) {
      try {
        Stream savedStream = sendStreamToServer(stream, notifyCreation);
        notifyLoaded(savedStream);
      } catch (ShootrException e) {
        handleServerError(e);
      }
    }
  }

  private String getStreamMode(Integer streamMode) {
    return streamMode == 0 ? StreamMode.PUBLIC : StreamMode.VIEW_ONLY;
  }

  private Stream streamFromParameters() {
    Stream stream = new Stream();

    stream.setCountry(localeProvider.getCountry());
    stream.setTitle(title);
    stream.setDescription(removeDescriptionLineBreaks(description));
    stream.setReadWriteMode(streamMode);
    String currentUserId = sessionRepository.getCurrentUserId();
    stream.setAuthorId(currentUserId);
    stream.setAuthorUsername(localUserRepository.getUserById(currentUserId).getUsername());
    stream.setTotalFollowers(0);
    stream.setTotalWatchers(0);
    if (idMedia != null) {
      stream.setPhotoIdMedia(idMedia);
    }
    return stream;
  }

  private Stream sendStreamToServer(Stream stream, boolean notify) {
    return remoteStreamRepository.putStream(stream, notify);
  }

  //region Validation
  private boolean validateStream(Stream stream) {
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
      return null;
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
