package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ShootrException;
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

public class NewEventInteractor implements Interactor{

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final TimezoneRepository timezoneRepository;
    private final EventRepository remoteEventRepository;

    private String title;
    private long startDate;
    private long endDate;
    private Callback callback;
    private InteractorErrorCallback errorCallback;

    @Inject public NewEventInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, TimezoneRepository timezoneRepository, @Remote EventRepository remoteEventRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.timezoneRepository = timezoneRepository;
        this.remoteEventRepository = remoteEventRepository;
    }

    public void createNewEvent(String title, long startDate, long endDate, Callback callback, InteractorErrorCallback errorCallback) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Event event = new Event();
        event.setTitle(title);
        event.setAuthorId(sessionRepository.getCurrentUserId());
        event.setStartDate(new Date(startDate));
        event.setEndDate(new Date(endDate));
        event.setTimezone(timezoneRepository.getCurrentTimezone().getID());

        if (validateEvent(event)) {
            try {
                Event savedEvent = sendEventToServer(event);
                //TODO change visible
                notifyLoaded(savedEvent);
            } catch (ShootrException e) {
                notifyError(e);
            }
        }
    }

    private Event sendEventToServer(Event event) {
        return remoteEventRepository.putEvent(event);
    }

    private void notifyLoaded(final Event event) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(event);
            }
        });
    }

    private boolean validateEvent(Event event) {
        List<FieldValidationError> validationErrors = new EventValidator().validate(event);
        if (validationErrors.isEmpty()) {
            return true;
        } else {
            notifyError(new DomainValidationException(validationErrors));
            return false;
        }
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
