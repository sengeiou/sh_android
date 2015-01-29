package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.LocalRepository;
import com.shootr.android.domain.repository.RemoteRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.List;
import javax.inject.Inject;

/**
 * Gives the number of people watching the event the current user has visible.
 */
public class WatchNumberInteractor implements Interactor{

    private EventsWatchedCountInteractor.Callback callback;
    private InteractorErrorCallback errorCallback;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final WatchRepository watchRepository;

    @Inject public WatchNumberInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @LocalRepository UserRepository userRepository, @RemoteRepository WatchRepository watchRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.watchRepository = watchRepository;
    }

    public void loadWatchNumber(EventsWatchedCountInteractor.Callback callback, InteractorErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Long currentVisibleEventId = getCurrentVisibleEventId();
        List<User> peopleAndMe = getPeopleAndMe();
        List<Watch> watchesForVisibleEvent = getWatches(currentVisibleEventId, peopleAndMe);
        Integer countIsWatching = countIsWatching(watchesForVisibleEvent);
        notifyLoaded(countIsWatching);
    }

    private void notifyLoaded(final Integer countIsWatching) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(countIsWatching);
            }
        });
    }

    protected Long getCurrentVisibleEventId() {
        Watch currentWatching = watchRepository.getCurrentVisibleWatch();
        return currentWatching.getIdEvent();
    }

    protected List<User> getPeopleAndMe() {
        List<User> people = userRepository.getPeople();
        people.add(sessionRepository.getCurrentUser());
        return people;
    }

    protected List<Watch> getWatches(Long eventId, List<User> users) {
        return watchRepository.getWatchesForUsersAndEvent(users, eventId);
    }

    protected Integer countIsWatching(List<Watch> watches) {
        int watchingCount = 0;
        for (Watch watch : watches) {
            if (watch.isWatching()) {
                watchingCount++;
            }
        }
        return watchingCount;
    }

    public interface Callback {

        void onLoaded(Integer integer);

    }
}
