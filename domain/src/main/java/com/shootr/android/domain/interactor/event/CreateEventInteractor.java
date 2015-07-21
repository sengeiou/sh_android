package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
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
        Event event = eventFromParameters();

        if (validateEvent(event)) {
            try {
                Event savedEvent = sendEventToServer(event, notifyCreation);
                notifyLoaded(savedEvent);
            } catch (ShootrException e) {
                handleServerError(e);
            }
        }
    }

    private Event eventFromParameters() {
        Event event;
        if (isNewEvent()) {
            event = new Event();
            event.setLocale(localeProvider.getLocale());
        } else {
            event = remoteStreamRepository.getStreamById(idEvent);
        }
        event.setTitle(title);
        event.setTag(shortTitle);
        String currentUserId = sessionRepository.getCurrentUserId();
        event.setAuthorId(currentUserId);
        event.setAuthorUsername(sessionRepository.getCurrentUser().getUsername());
        return event;
    }

    private boolean isNewEvent() {
        return idEvent == null;
    }

    private Event sendEventToServer(Event event, boolean notify) {
        return remoteStreamRepository.putStream(event, notify);
    }

    //region Validation
    private boolean validateEvent(Event event) {
        List<FieldValidationError> validationErrors = new EventValidator().validate(event);
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

    private void notifyLoaded(final Event event) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(event);
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
        void onLoaded(Event event);
    }
}
