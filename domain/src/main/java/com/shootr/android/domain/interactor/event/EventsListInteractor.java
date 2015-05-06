package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventListSynchronizationRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.utils.TimeUtils;
import java.util.List;
import javax.inject.Inject;

public class EventsListInteractor implements Interactor {

    private static final Long REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS = 30L * 1000L;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventSearchRepository remoteEventSearchRepository;
    private final EventSearchRepository localEventSearchRepository;
    private final EventListSynchronizationRepository timelineSynchronizationRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final TimeUtils timeUtils;

    private Callback<EventSearchResultList> callback;
    private ErrorCallback errorCallback;

    @Inject public EventsListInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote EventSearchRepository remoteEventSearchRepository,
      @Local EventSearchRepository localEventSearchRepository, EventListSynchronizationRepository eventListSynchronizationRepository,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository, TimeUtils timeUtils) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteEventSearchRepository = remoteEventSearchRepository;
        this.localEventSearchRepository = localEventSearchRepository;
        this.timelineSynchronizationRepository = eventListSynchronizationRepository;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.timeUtils = timeUtils;
    }

    public void loadEvents(Callback<EventSearchResultList> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        List<EventSearchResult> localEvents = localEventSearchRepository.getDefaultEvents();
        notifyLoaded(localEvents);

        Long currentTime = timeUtils.getCurrentTime();
        if (localEvents.isEmpty() || minimumRefreshTimePassed(currentTime)) {
            try {
                refreshEvents();
                timelineSynchronizationRepository.setEventsRefreshDate(currentTime);
            } catch (ShootrException error) {
                notifyError(error);
            }
        }
    }

    protected void refreshEvents() {
        List<EventSearchResult> remoteEvents = remoteEventSearchRepository.getDefaultEvents();
        notifyLoaded(remoteEvents);
        localEventSearchRepository.deleteDefaultEvents();
        localEventSearchRepository.putDefaultEvents(remoteEvents);
    }

    private boolean minimumRefreshTimePassed(Long currentTime) {
        Long eventsLastRefreshDate = timelineSynchronizationRepository.getEventsRefreshDate();
        Long minimumTimeToRefresh = eventsLastRefreshDate + REFRESH_THRESHOLD_30_SECONDS_IN_MILLIS;
        return minimumTimeToRefresh < currentTime;
    }

    //region Result
    private void notifyLoaded(final List<EventSearchResult> results) {
        final EventSearchResultList searchResultList = new EventSearchResultList(results, getVisibleEventId());
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(searchResultList);
            }
        });
    }

    private String getVisibleEventId() {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        return currentUser.getVisibleEventId();
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
    //endregion
}
