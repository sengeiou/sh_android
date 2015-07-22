package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.domain.validation.EventValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import java.util.List;
import javax.inject.Inject;

public class CreateEventInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final StreamRepository remoteStreamRepository;
    private final LocaleProvider localeProvider;

    private String idEvent;
    private String title;
    private String shortTitle;
    private boolean notifyCreation;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public CreateEventInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @Remote StreamRepository remoteStreamRepository, LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localeProvider = localeProvider;
    }

    public void sendEvent(String idEvent, String title, String shortTitle, boolean notifyCreation,
      Callback callback, ErrorCallback errorCallback) {
        this.idEvent = idEvent;
        this.title = title;
        this.shortTitle = shortTitle;
        this.notifyCreation = notifyCreation;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        Stream stream = eventFromParameters();

        if (validateEvent(stream)) {
            try {
                Stream savedStream = sendEventToServer(stream, notifyCreation);
                notifyLoaded(savedStream);
            } catch (ShootrException e) {
                handleServerError(e);
            }
        }
    }

    private Stream eventFromParameters() {
        Stream stream;
        if (isNewEvent()) {
            stream = new Stream();
            stream.setLocale(localeProvider.getLocale());
        } else {
            stream = remoteStreamRepository.getStreamById(idEvent);
        }
        stream.setTitle(title);
        stream.setTag(shortTitle);
        String currentUserId = sessionRepository.getCurrentUserId();
        stream.setAuthorId(currentUserId);
        stream.setAuthorUsername(sessionRepository.getCurrentUser().getUsername());
        return stream;
    }

    private boolean isNewEvent() {
        return idEvent == null;
    }

    private Stream sendEventToServer(Stream stream, boolean notify) {
        return remoteStreamRepository.putStream(stream, notify);
    }

    //region Validation
    private boolean validateEvent(Stream stream) {
        List<FieldValidationError> validationErrors = new EventValidator().validate(stream);
        if (validationErrors.isEmpty()) {
            return true;
        } else {
            notifyError(new DomainValidationException(validationErrors));
            return false;
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
            case ShootrError.ERROR_CODE_EVENT_TITLE_TOO_SHORT:
            case ShootrError.ERROR_CODE_EVENT_TITLE_TOO_LONG:
                return EventValidator.FIELD_TITLE;
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
