package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrValidationException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventsSearchInteractor implements Interactor {

    public static final int MIN_SEARCH_LENGTH = 3;
    private final InteractorHandler interactorHandler;
    private final SessionRepository sessionRepository;
    private final EventSearchRepository eventSearchRepository;
    private final EventRepository localEventRepository;
    private final PostExecutionThread postExecutionThread;

    private String query;
    private Callback callback;
    private InteractorErrorCallback errorCallback;

    @Inject public EventsSearchInteractor(InteractorHandler interactorHandler, SessionRepository sessionRepository,
      EventSearchRepository eventSearchRepository, @Local EventRepository localEventRepository,
      PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.sessionRepository = sessionRepository;
        this.eventSearchRepository = eventSearchRepository;
        this.localEventRepository = localEventRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void searchEvents(String query, Callback callback, InteractorErrorCallback errorCallback) {
        this.query = query;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        removeInvalidCharactersFromQuery();
        if (validateSearchQuery()) {
            performSearch();
        }
    }

    private void removeInvalidCharactersFromQuery() {
        query = query.replace("%", "").trim();
    }

    private boolean validateSearchQuery() {
        if (query == null || query.length() < MIN_SEARCH_LENGTH) {
            notifyError(new ShootrValidationException(ShootrError.ERROR_CODE_SEARCH_TOO_SHORT));
            return false;
        }
        return true;
    }

    private void performSearch() {
        List<EventSearchResult> events = eventSearchRepository.getEvents(query);
        events = filterEventsNotMatchingQuery(events);

        EventSearchResultList eventSearchResultList = new EventSearchResultList(events);

        setCurrentVisibleEventIfAny(eventSearchResultList);

        notifySearchResultsSuccessful(eventSearchResultList);
    }

    private void setCurrentVisibleEventIfAny(EventSearchResultList eventSearchResultList) {
        Long eventWatchingId = sessionRepository.getCurrentUser().getVisibleEventId();
        if (eventWatchingId != null) {
            Event visibleEvent = localEventRepository.getEventById(eventWatchingId);
            if (visibleEvent != null) {
                eventSearchResultList.setCurrentVisibleEvent(visibleEvent);
            }
        }
    }

    private List<EventSearchResult> filterEventsNotMatchingQuery(List<EventSearchResult> events) {
        List<EventSearchResult> filteredResults = new ArrayList<>(events.size());
        for (EventSearchResult event : events) {
            if (matchesQuery(event)) {
                filteredResults.add(event);
            }
        }
        return filteredResults;
    }

    private boolean matchesQuery(EventSearchResult event) {
        return event.getEvent().getTitle().toLowerCase().contains(query.toLowerCase());
    }

    private void notifySearchResultsSuccessful(final EventSearchResultList events) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(events);
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

        void onLoaded(EventSearchResultList results);
    }
}
