package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.LocalRepository;
import com.shootr.android.domain.repository.RemoteRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.List;
import javax.inject.Inject;

public class VisibleEventInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository userRepository;
    private final WatchRepository remoteWatchRepository;
    private final WatchRepository localWatchRepository;
    private final EventRepository remoteEventRepository;
    private final EventRepository localEventRepository;
    private Callback callback;

    @Inject public VisibleEventInfoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @LocalRepository UserRepository userRepository,
      @RemoteRepository WatchRepository remoteWatchRepository, @LocalRepository WatchRepository localWatchRepository,
      @RemoteRepository EventRepository remoteEventRepository, @LocalRepository EventRepository localEventRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.userRepository = userRepository;
        this.remoteWatchRepository = remoteWatchRepository;
        this.localWatchRepository = localWatchRepository;
        this.remoteEventRepository = remoteEventRepository;
        this.localEventRepository = localEventRepository;
    }

    public void obtainEventInfo(Callback callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        obtainLocalEventInfo();
        obtainRemoteEventInfo();
    }

    protected void obtainLocalEventInfo() {
        EventInfo eventInfo = getEventInfo(localWatchRepository, localEventRepository);
        notifyLoaded(eventInfo);
    }

    protected void obtainRemoteEventInfo() {
        EventInfo eventInfo = getEventInfo(remoteWatchRepository, remoteEventRepository);
        notifyLoaded(eventInfo);
    }

    protected EventInfo getEventInfo(WatchRepository watchRepository, EventRepository eventRepository) {
        Watch currentVisibleWatch = watchRepository.getCurrentVisibleWatch();
        Event visibleEvent = eventRepository.getEventById(currentVisibleWatch.getIdEvent());

        List<User> people = userRepository.getPeople();
        List<Watch> watchesFromPeople = watchRepository.getWatchesFromUsersAndEvent(people, visibleEvent.getId());

        return buildEventInfo(visibleEvent, currentVisibleWatch, watchesFromPeople);
    }

    private EventInfo buildEventInfo(Event currentVisibleEvent, Watch visibleEventWatch, List<Watch> followingWatches) {
        return EventInfo.builder()
          .event(currentVisibleEvent)
          .currentUserWatch(visibleEventWatch)
          .watchers(followingWatches)
          .build();
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
