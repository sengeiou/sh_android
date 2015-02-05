package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.TimezoneRepository;
import com.shootr.android.domain.validation.EventValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class CreateEventInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final TimezoneRepository timezoneRepository;
    private final EventRepository remoteEventRepository;

    private Long idEvent;
    private String title;
    private long startDate;
    private long endDate;
    private Callback callback;
    private InteractorErrorCallback errorCallback;

    @Inject public CreateEventInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, TimezoneRepository timezoneRepository,
      @Remote EventRepository remoteEventRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.timezoneRepository = timezoneRepository;
        this.remoteEventRepository = remoteEventRepository;
    }

    public void sendEvent(Long idEvent, String title, long startDate, long endDate, Callback callback,
      InteractorErrorCallback errorCallback) {
        this.idEvent = idEvent;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Event event = eventFromParameters();

        if (validateEvent(event)) {
            try {
                Event savedEvent = sendEventToServer(event);
                notifyLoaded(savedEvent);
            } catch (ShootrException e) {
                handleServerError(e);
            }
        }
    }

    private Event eventFromParameters() {
        Event event = new Event();
        event.setId(idEvent);
        event.setTitle(title);
        event.setAuthorId(sessionRepository.getCurrentUserId());
        event.setStartDate(new Date(startDate));
        event.setEndDate(new Date(endDate));
        event.setTimezone(timezoneRepository.getCurrentTimezone().getID());
        return event;
    }

    private Event sendEventToServer(Event event) {
        return remoteEventRepository.putEvent(event);
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
            case ShootrError.ERROR_CODE_EVENT_START_DATE_TOO_LATE:
                return EventValidator.FIELD_START_DATE;
            case ShootrError.ERROR_CODE_EVENT_END_DATE_BEFORE_NOW:
            case ShootrError.ERROR_CODE_EVENT_END_DATE_BEFORE_START:
            case ShootrError.ERROR_CODE_EVENT_END_DATE_TOO_LATE:
                return EventValidator.FIELD_END_DATE;
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
