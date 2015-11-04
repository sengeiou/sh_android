package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.DomainValidationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.List;
import javax.inject.Inject;

public class CreateStreamInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    private final LocaleProvider localeProvider;

    private String idStream;
    private String title;
    private String shortTitle;
    private String description;
    private boolean notifyCreation;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public CreateStreamInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.repository.SessionRepository sessionRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository,
      @Local com.shootr.mobile.domain.repository.StreamRepository localStreamRepository,
      LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localStreamRepository = localStreamRepository;
        this.localeProvider = localeProvider;
    }

    public void sendStream(String idStream, String title, String shortTitle, String description, boolean notifyCreation,
      Callback callback, ErrorCallback errorCallback) {
        this.idStream = idStream;
        this.title = title;
        this.shortTitle = shortTitle;
        this.description = description;
        this.notifyCreation = notifyCreation;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        com.shootr.mobile.domain.Stream stream = streamFromParameters();
        if (validateStream(stream)) {
            try {
                com.shootr.mobile.domain.Stream savedStream = sendStreamToServer(stream, notifyCreation);
                notifyLoaded(savedStream);
            } catch (com.shootr.mobile.domain.exception.ShootrException e) {
                handleServerError(e);
            }
        }
    }

    private com.shootr.mobile.domain.Stream streamFromParameters() {
        com.shootr.mobile.domain.Stream stream;
        if (isNewStream()) {
            stream = new com.shootr.mobile.domain.Stream();
            stream.setCountry(localeProvider.getCountry());
        } else {
            stream = localStreamRepository.getStreamById(idStream);
        }
        stream.setTitle(title);
        stream.setShortTitle(shortTitle);
        stream.setDescription(removeDescriptionLineBreaks(description));
        String currentUserId = sessionRepository.getCurrentUserId();
        stream.setAuthorId(currentUserId);
        stream.setAuthorUsername(sessionRepository.getCurrentUser().getUsername());
        stream.setTotalFavorites(0);
        return stream;
    }

    private boolean isNewStream() {
        return idStream == null;
    }

    private com.shootr.mobile.domain.Stream sendStreamToServer(com.shootr.mobile.domain.Stream stream, boolean notify) {
        return remoteStreamRepository.putStream(stream, notify);
    }

    //region Validation
    private boolean validateStream(com.shootr.mobile.domain.Stream stream) {
        List<com.shootr.mobile.domain.validation.FieldValidationError> validationErrors = new com.shootr.mobile.domain.validation.StreamValidator().validate(stream);
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

    private void handleServerError(com.shootr.mobile.domain.exception.ShootrException e) {
        if (e.getCause() instanceof com.shootr.mobile.domain.exception.ShootrServerException) {
            com.shootr.mobile.domain.exception.ShootrServerException
              serverException = (com.shootr.mobile.domain.exception.ShootrServerException) e.getCause();
            String errorCode = serverException.getShootrError().getErrorCode();
            com.shootr.mobile.domain.validation.FieldValidationError validationError = validationErrorFromCode(errorCode);
            if (validationError != null) {
                notifyError(new DomainValidationException(validationError));
                return;
            }
        }
        notifyError(e);
    }

    private com.shootr.mobile.domain.validation.FieldValidationError validationErrorFromCode(String errorCode) {
        int field = fieldFromErrorCode(errorCode);
        if (field != 0) {
            return new com.shootr.mobile.domain.validation.FieldValidationError(errorCode, field);
        } else {
            return null;
        }
    }

    private int fieldFromErrorCode(String errorCode) {
        switch (errorCode) {
            case com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_STREAM_TITLE_TOO_SHORT:
            case com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_STREAM_TITLE_TOO_LONG:
                return com.shootr.mobile.domain.validation.StreamValidator.FIELD_TITLE;
        }
        return 0;
    }
    //endregion

    private void notifyLoaded(final com.shootr.mobile.domain.Stream stream) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(stream);
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }

    public interface Callback {
        void onLoaded(com.shootr.mobile.domain.Stream stream);
    }
}
