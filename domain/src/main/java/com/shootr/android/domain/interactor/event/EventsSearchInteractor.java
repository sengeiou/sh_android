package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrValidationException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventsSearchInteractor implements Interactor {

    public static final int MIN_SEARCH_LENGTH = 3;
    private final InteractorHandler interactorHandler;
    private final SessionRepository sessionRepository;
    private final EventSearchRepository eventSearchRepository;
    private final PostExecutionThread postExecutionThread;
    private final LocaleProvider localeProvider;

    private String query;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject
    public EventsSearchInteractor(InteractorHandler interactorHandler, SessionRepository sessionRepository,
      @Remote EventSearchRepository eventSearchRepository,
      PostExecutionThread postExecutionThread, LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.sessionRepository = sessionRepository;
        this.eventSearchRepository = eventSearchRepository;
        this.postExecutionThread = postExecutionThread;
        this.localeProvider = localeProvider;
    }

    public void searchEvents(String query, Callback callback, ErrorCallback errorCallback) {
        this.query = query;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        removeInvalidCharactersFromQuery();
        if (validateSearchQuery()) {
            try {
                performSearch();
            } catch (ServerCommunicationException networkError) {
                notifyError(networkError);
            }
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
        List<EventSearchResult> events = eventSearchRepository.getEvents(query, localeProvider.getLocale());
        events = filterEventsNotMatchingQuery(events);

        EventSearchResultList eventSearchResultList = new EventSearchResultList(events);

        setCurrentCheckedInEventIfAny(eventSearchResultList);

        notifySearchResultsSuccessful(eventSearchResultList);
    }

    private void setCurrentCheckedInEventIfAny(EventSearchResultList eventSearchResultList) {
        String eventCheckedIn = sessionRepository.getCurrentUser().getIdCheckedEvent();
        if (eventCheckedIn != null) {
            eventSearchResultList.setCurrentCheckedInEventId(eventCheckedIn);
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
