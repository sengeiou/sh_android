package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.LocalRepository;
import com.shootr.android.domain.repository.RemoteRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

/**
 * Counts the number of events being watched by the people I follow.
 */
public class EventsWatchedCountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final WatchRepository remoteWatchRepository;
    private final UserRepository localUserRepository;
    private Callback callback;
    private InteractorErrorCallback interactorErrorCallback;

    @Inject public EventsWatchedCountInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @RemoteRepository WatchRepository remoteWatchRepository,
      @LocalRepository UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.remoteWatchRepository = remoteWatchRepository;
        this.localUserRepository = localUserRepository;
    }

    public void obtainEventsCount(Callback callback, InteractorErrorCallback interactorErrorCallback) {
        this.callback = callback;
        this.interactorErrorCallback = interactorErrorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        List<User> peopleFollowing = getPeopleFollowing();
        peopleFollowing.add(sessionRepository.getCurrentUser());
        List<Watch> watchesFromPeople = getWatchesFromUsers(peopleFollowing);
        Integer eventsCount = countDifferentEventsInWatches(watchesFromPeople);
        notifyOnLoaded(eventsCount);
    }

    private List<Watch> getWatchesFromUsers(List<User> peopleFollowingIds) {
        return remoteWatchRepository.getWatchesFromUsers(peopleFollowingIds);
    }

    private List<User> getPeopleFollowing() {
        return localUserRepository.getPeople();
    }

    protected Integer countDifferentEventsInWatches(List<Watch> watches) {
        Set<Long> eventIdsSet = new HashSet<>();
        for (Watch watch : watches) {
            if (watch.isWatching()) {
                eventIdsSet.add(watch.getIdEvent());
            }
        }
        return eventIdsSet.size();
    }

    private List<Long> idsFromUsers(List<User> peopleFollowing) {
        List<Long> ids = new ArrayList<>(peopleFollowing.size());
        for (User user : peopleFollowing) {
            ids.add(user.getIdUser());
        }
        return ids;
    }

    //region Callbacks
    public void notifyOnLoaded(final Integer count) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(count);
            }
        });
    }

    public void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                interactorErrorCallback.onError(error);
            }
        });
    }

    public interface Callback {

        void onLoaded(Integer count);

    }
    //endregion
}
