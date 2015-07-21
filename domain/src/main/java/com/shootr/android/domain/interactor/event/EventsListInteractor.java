package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventListSynchronizationRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import com.shootr.android.domain.utils.TimeUtils;
import java.util.List;
import javax.inject.Inject;

public class EventsListInteractor implements Interactor {

    private static final Long REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS = 30L * 1000L;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventSearchRepository remoteEventSearchRepository;
    private final EventSearchRepository localEventSearchRepository;
    private final EventListSynchronizationRepository eventListSynchronizationRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final TimeUtils timeUtils;
    private final LocaleProvider localeProvider;
    private final WatchersRepository watchersRepository;
    private final StreamRepository localStreamRepository;

    private Callback<EventSearchResultList> callback;
    private ErrorCallback errorCallback;

    @Inject
    public EventsListInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote EventSearchRepository remoteEventSearchRepository,
      @Local EventSearchRepository localEventSearchRepository,
      EventListSynchronizationRepository eventListSynchronizationRepository,
      @Local StreamRepository localStreamRepository,
      @Local WatchersRepository watchersRepository,
      SessionRepository sessionRepository,
      @Local UserRepository localUserRepository,
      TimeUtils timeUtils,
      LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteEventSearchRepository = remoteEventSearchRepository;
        this.localEventSearchRepository = localEventSearchRepository;
        this.eventListSynchronizationRepository = eventListSynchronizationRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.timeUtils = timeUtils;
        this.localeProvider = localeProvider;
        this.watchersRepository = watchersRepository;
        this.localStreamRepository = localStreamRepository;
    }

    public void loadEvents(Callback<EventSearchResultList> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        List<EventSearchResult> localEvents = localEventSearchRepository.getDefaultEvents(localeProvider.getLocale());
        notifyLoaded(localEvents);

        Long currentTime = timeUtils.getCurrentTime();
        if (localEvents.isEmpty() || minimumRefreshTimePassed(currentTime)) {
            try {
                refreshEvents();
                eventListSynchronizationRepository.setEventsRefreshDate(currentTime);
            } catch (ShootrException error) {
                notifyError(error);
            }
        }
    }

    protected void refreshEvents() {
        List<EventSearchResult> remoteEvents = remoteEventSearchRepository.getDefaultEvents(localeProvider.getLocale());
        notifyLoaded(remoteEvents);
        localEventSearchRepository.deleteDefaultEvents();
        localEventSearchRepository.putDefaultEvents(remoteEvents);
    }

    private boolean minimumRefreshTimePassed(Long currentTime) {
        Long eventsLastRefreshDate = eventListSynchronizationRepository.getEventsRefreshDate();
        Long minimumTimeToRefresh = eventsLastRefreshDate + REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS;
        return minimumTimeToRefresh < currentTime;
    }

    //region Result
    private void notifyLoaded(final List<EventSearchResult> results) {
        final EventSearchResultList searchResultList =
          new EventSearchResultList(results, getWatchingEventWithWatchNumber());
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(searchResultList);
            }
        });
    }

    private EventSearchResult getWatchingEventWithWatchNumber() {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        String idWatchingEvent = currentUser.getIdWatchingEvent();
        if (idWatchingEvent != null) {
            Event event = localStreamRepository.getStreamById(idWatchingEvent);
            Integer watchers = watchersRepository.getWatchers(idWatchingEvent);
            EventSearchResult eventSearchResult = new EventSearchResult();
            eventSearchResult.setEvent(event);
            eventSearchResult.setWatchersNumber(watchers);
            return eventSearchResult;
        } else {
            return null;
        }
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }
    //endregion
}
