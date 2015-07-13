package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class VisibleEventInfoInteractor implements Interactor {

    private static final String VISIBLE_EVENT = null;
    private static final EventInfo NO_EVENT_VISIBLE_INFO = null;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localWatchRepository;
    private final EventRepository remoteEventRepository;
    private final EventRepository localEventRepository;
    private final SessionRepository sessionRepository;
    private ErrorCallback errorCallback;

    private String idEventWanted;
    private Callback callback;

    @Inject public VisibleEventInfoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localWatchRepository,
      @Remote EventRepository remoteEventRepository, @Local EventRepository localEventRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localWatchRepository = localWatchRepository;
        this.remoteEventRepository = remoteEventRepository;
        this.localEventRepository = localEventRepository;
        this.sessionRepository = sessionRepository;
    }

    //TODO this interactor is WRONG. Should NOT have two different opperations. Separate them!
    public void obtainEventInfo(String idEventWanted, Callback callback, ErrorCallback errorCallback) {
        this.idEventWanted = idEventWanted;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    public void obtainVisibleEventInfo(Callback callback, ErrorCallback errorCallback) {
        obtainEventInfo(VISIBLE_EVENT, callback, errorCallback);
    }

    @Override public void execute() throws Exception {
        try {
            obtainLocalEventInfo();
            obtainRemoteEventInfo();
        }catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    protected void obtainLocalEventInfo() {
        EventInfo eventInfo = getEventInfo(localWatchRepository, localEventRepository);
        if (eventInfo != NO_EVENT_VISIBLE_INFO) {
            notifyLoaded(eventInfo);
        }
    }

    protected void obtainRemoteEventInfo() {
        EventInfo eventInfo = getEventInfo(remoteUserRepository, remoteEventRepository);
        if (eventInfo != null) {
            notifyLoaded(eventInfo);
        } else {
            notifyLoaded(noEvent());
        }
    }

    protected EventInfo getEventInfo(UserRepository userRepository, EventRepository eventRepository) {
        User currentUser = userRepository.getUserById(sessionRepository.getCurrentUserId());

        String wantedEventId = getWantedEventId(currentUser);

        if (wantedEventId != null) {
            Event visibleEvent = eventRepository.getEventById(wantedEventId);
            if (visibleEvent == null) {
                //TODO should not happen, but can't assert that right now
                return NO_EVENT_VISIBLE_INFO;
            }

            List<User> people = userRepository.getPeople();
            List<User> watchesFromPeople = filterUsersWatchingEvent(people, wantedEventId);
            watchesFromPeople = sortWatchersListByJoinEventDate(watchesFromPeople);
            return buildEventInfo(visibleEvent, watchesFromPeople, currentUser);
        }
        return NO_EVENT_VISIBLE_INFO;
    }

    private List<User> sortWatchersListByJoinEventDate(List<User> watchesFromPeople) {
        Collections.sort(watchesFromPeople, new Comparator<User>() {
            @Override
            public int compare(User userModel, User t1) {
                return t1.getJoinEventDate().compareTo(userModel.getJoinEventDate());
            }
        });
        return watchesFromPeople;
    }

    private String getWantedEventId(User currentUser) {
        if (idEventWanted != null && !idEventWanted.equals(VISIBLE_EVENT)) {
            return idEventWanted;
        } else {
            return currentUser.getIdWatchingEvent();
        }
    }

    protected List<User> filterUsersWatchingEvent(List<User> people, String idEvent) {
        List<User> watchers = new ArrayList<>();
        for (User user : people) {
            if (idEvent.equals(user.getIdWatchingEvent())) {
                watchers.add(user);
            }
        }
        return watchers;
    }

    private EventInfo buildEventInfo(Event currentVisibleEvent, List<User> eventWatchers, User currentUser) {
        boolean isCurrentUserWatching = currentVisibleEvent.getId().equals(currentUser.getIdWatchingEvent());
        return EventInfo.builder()
          .event(currentVisibleEvent)
          .watchers(eventWatchers)
          .currentUserWatching(isCurrentUserWatching ? currentUser : null)
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

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }

    public interface Callback {

        void onLoaded(EventInfo eventInfo);
    }
}
