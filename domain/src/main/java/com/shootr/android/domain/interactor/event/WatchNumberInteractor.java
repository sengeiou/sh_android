package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
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
    private ErrorCallback errorCallback;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;

    @Inject public WatchNumberInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository,
      @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
    }

    public void loadWatchNumber(Callback callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        String currentVisibleEventId = getCurrentVisibleEventId();
        if (currentVisibleEventId == null) {
            notifyLoaded(NO_EVENT);
            return;
        }
        List<User> peopleAndMe = getPeopleAndMe();
        List<User> watchers = filterUsersWatchingEvent(peopleAndMe, currentVisibleEventId);
        notifyLoaded(watchers.size());
    }

    protected List<User> filterUsersWatchingEvent(List<User> people, String idEvent) {
        List<User> watchers = new ArrayList<>();
        for (User user : people) {
            if (idEvent.equals(user.getIdWatchingStream())) {
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

    protected String getCurrentVisibleEventId() {
        User currentUser = sessionRepository.getCurrentUser();
        return currentUser.getIdWatchingStream();
    }

    //TODO want local or remote?
    protected List<User> getPeopleAndMe() {
        List<User> peopleAndMe = new ArrayList<>();
        List<User> people = getRemotePeopleOrFallbackToLocal();
        peopleAndMe.addAll(people);
        peopleAndMe.add(sessionRepository.getCurrentUser());
        return peopleAndMe;
    }

    private List<User> getRemotePeopleOrFallbackToLocal() {
        try {
            return remoteUserRepository.getPeople();
        } catch (ServerCommunicationException networkError) {
            return localUserRepository.getPeople();
        }
    }

    public interface Callback {

        void onLoaded(Integer count);

    }
}
