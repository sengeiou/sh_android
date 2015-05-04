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
import com.shootr.android.domain.validation.EventValidator;
import com.shootr.android.domain.validation.FieldValidationError;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class CreateEventInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final EventRepository remoteEventRepository;

    private String idEvent;
    private String title;
    private long startDate;
    private long endDate;
    private String timezoneId;
    private boolean notifyCreation;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public CreateEventInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @Remote EventRepository remoteEventRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.remoteEventRepository = remoteEventRepository;
    }

    public void sendEvent(String idEvent, String title, long startDate, long endDate, String timezoneId,
      boolean notifyCreation, Callback callback, ErrorCallback errorCallback) {
        this.idEvent = idEvent;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timezoneId = timezoneId;
        this.notifyCreation = notifyCreation;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
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
        } else {
            event = remoteEventRepository.getEventById(idEvent);
        }
        event.setTitle(title);
        String currentUserId = sessionRepository.getCurrentUserId();
        event.setAuthorId(currentUserId.toString());
        event.setAuthorUsername(sessionRepository.getCurrentUser().getUsername());
        event.setStartDate(new Date(startDate));
        event.setEndDate(new Date(endDate));
        event.setTimezone(timezoneId);
        event.setTag(makeTag(title));
        return event;
    }

    private boolean isNewEvent() {
        return idEvent == null;
    }

    private String makeTag(String title) {
        String filteredTitle = filterTitle(title);
        String camelCaseTitle = toCamelCase(filteredTitle);
        if (camelCaseTitle.length() > EventValidator.TAG_MAXIMUM_LENGTH) {
            return camelCaseTitle.substring(0, EventValidator.TAG_MAXIMUM_LENGTH);
        } else {
            return camelCaseTitle;
        }
    }

    private String filterTitle(String title) {
        return title.replaceAll("[^A-Za-z0-9 ]", "");
    }

    private String toCamelCase(String text) {
        String[] words = text.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words) {
            stringBuilder.append(toProperCase(word));
        }
        return stringBuilder.toString();
    }

    private String toProperCase(String word) {
        if (word.length() > 0) {
            return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        } else {
            return word;
        }
    }

    private Event sendEventToServer(Event event, boolean notify) {
        return remoteEventRepository.putEvent(event, notify);
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
