package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Gives the number of people watching the event the current user has visible.
 */
public class WatchNumberInteractor implements Interactor{

    public static final int NO_EVENT = -1;
    private Callback callback;
    private InteractorErrorCallback errorCallback;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;

    @Inject public WatchNumberInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
    }

    public void loadWatchNumber(Callback callback, InteractorErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Long currentVisibleEventId = getCurrentVisibleEventId();
        if (currentVisibleEventId == null) {
            notifyLoaded(NO_EVENT);
            return;
        }
        List<User> peopleAndMe = getPeopleAndMe();
        List<User> watchers = filterUsersWatchingEvent(peopleAndMe, currentVisibleEventId);
        notifyLoaded(watchers.size());
    }

    protected List<User> filterUsersWatchingEvent(List<User> people, Long idEvent) {
        List<User> watchers = new ArrayList<>();
        for (User user : people) {
            if (idEvent.equals(user.getVisibleEventId())) {
                watchers.add(user);
            }
        }
        return watchers;
    }

    private void notifyLoaded(final Integer countIsWatching) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(countIsWatching);
            }
        });
    }

    protected Long getCurrentVisibleEventId() {
        User currentUser = sessionRepository.getCurrentUser();
        return currentUser.getVisibleEventId();
    }

    //TODO want local or remote?
    protected List<User> getPeopleAndMe() {
        List<User> peopleAndMe = new ArrayList<>();
        List<User> people = localUserRepository.getPeople();
        peopleAndMe.addAll(people);
        peopleAndMe.add(sessionRepository.getCurrentUser());
        return peopleAndMe;
    }

    public interface Callback {

        void onLoaded(Integer count);

    }
}
