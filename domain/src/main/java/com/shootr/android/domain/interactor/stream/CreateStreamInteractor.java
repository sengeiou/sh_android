package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.domain.validation.FieldValidationError;
import com.shootr.android.domain.validation.StreamValidator;
import java.util.List;
import javax.inject.Inject;

public class CreateStreamInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final StreamRepository remoteStreamRepository;
    private final StreamRepository localStreamRepository;
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
      SessionRepository sessionRepository,
      @Remote StreamRepository remoteStreamRepository,
      @Local StreamRepository localStreamRepository,
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

    private Stream streamFromParameters() {
        Stream stream;
        if (isNewStream()) {
            stream = new Stream();
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
