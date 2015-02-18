package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.List;
import javax.inject.Inject;

public class VisibleEventInfoInteractor implements Interactor {

    private static final long VISIBLE_EVENT = -1;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final WatchRepository remoteWatchRepository;
    private final WatchRepository localWatchRepository;
    private final EventRepository remoteEventRepository;
    private final EventRepository localEventRepository;
    private final SessionRepository sessionRepository;
    private long idEvent;
    private Callback callback;

    @Inject public VisibleEventInfoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local UserRepository localUserRepository,
      @Remote WatchRepository remoteWatchRepository, @Local WatchRepository localWatchRepository,
      @Remote EventRepository remoteEventRepository, @Local EventRepository localEventRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.remoteWatchRepository = remoteWatchRepository;
        this.localWatchRepository = localWatchRepository;
        this.remoteEventRepository = remoteEventRepository;
        this.localEventRepository = localEventRepository;
        this.sessionRepository = sessionRepository;
    }

    public void obtainEventInfo(long idEvent, Callback callback) {
        this.idEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    public void obtainVisibleEventInfo(Callback callback) {
        obtainEventInfo(VISIBLE_EVENT, callback);
    }

    @Override public void execute() throws Throwable {
        obtainLocalEventInfo();
        obtainRemoteEventInfo();
    }

    protected void obtainLocalEventInfo() {
        EventInfo eventInfo = getEventInfo(localWatchRepository, localEventRepository);
        if (eventInfo != null) {
            notifyLoaded(eventInfo);
        } else {
            notifyLoaded(noEvent());
        }
    }

    protected void obtainRemoteEventInfo() {
        EventInfo eventInfo = getEventInfo(remoteWatchRepository, remoteEventRepository);
        if (eventInfo != null) {
            notifyLoaded(eventInfo);
        } else {
            notifyLoaded(noEvent());
        }
    }

    protected EventInfo getEventInfo(WatchRepository watchRepository, EventRepository eventRepository) {
        Watch currentVisibleWatch = getEventWatch(watchRepository);

        Event visibleEvent = eventRepository.getEventById(idEvent);
        if (visibleEvent == null) {
            return null;
        }

        List<User> people = localUserRepository.getPeople();
        List<Watch> watchesFromPeople = watchRepository.getWatchesForUsersAndEvent(people, visibleEvent.getId());

        return buildEventInfo(visibleEvent, currentVisibleWatch, watchesFromPeople);
    }

    private Watch getEventWatch(WatchRepository watchRepository) {
        if (idEvent > 0) {
            return watchRepository.getWatchForUserAndEvent(sessionRepository.getCurrentUser(), idEvent);
        } else {
            Watch currentVisibleWatch = watchRepository.getCurrentVisibleWatch();
            if (currentVisibleWatch != null) {
                idEvent = currentVisibleWatch.getIdEvent();
            }
            return currentVisibleWatch;
        }
    }

    private EventInfo buildEventInfo(Event currentVisibleEvent, Watch visibleEventWatch, List<Watch> followingWatches) {
        return EventInfo.builder()
          .event(currentVisibleEvent)
          .currentUserWatch(visibleEventWatch)
          .watchers(followingWatches)
          .build();
    }

    private EventInfo noEvent() {
        return new EventInfo();
    }

    private void notifyLoaded(final EventInfo eventInfo) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(eventInfo);
            }
        });
    }

    public interface Callback {

        void onLoaded(EventInfo eventInfo);
    }
}
