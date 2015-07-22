package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
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
    private static final StreamInfo NO_EVENT_VISIBLE_INFO = null;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localWatchRepository;
    private final StreamRepository remoteStreamRepository;
    private final StreamRepository localStreamRepository;
    private final SessionRepository sessionRepository;
    private ErrorCallback errorCallback;

    private String idEventWanted;
    private Callback callback;

    @Inject public VisibleEventInfoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localWatchRepository,
      @Remote StreamRepository remoteStreamRepository, @Local StreamRepository localStreamRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.localWatchRepository = localWatchRepository;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localStreamRepository = localStreamRepository;
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
        StreamInfo streamInfo = getEventInfo(localWatchRepository, localStreamRepository);
        if (streamInfo != NO_EVENT_VISIBLE_INFO) {
            notifyLoaded(streamInfo);
        }
    }

    protected void obtainRemoteEventInfo() {
        StreamInfo streamInfo = getEventInfo(remoteUserRepository, remoteStreamRepository);
        if (streamInfo != null) {
            notifyLoaded(streamInfo);
        } else {
            notifyLoaded(noEvent());
        }
    }

    protected StreamInfo getEventInfo(UserRepository userRepository, StreamRepository streamRepository) {
        User currentUser = userRepository.getUserById(sessionRepository.getCurrentUserId());

        String wantedEventId = getWantedEventId(currentUser);

        if (wantedEventId != null) {
            Stream visibleStream = streamRepository.getStreamById(wantedEventId);
            if (visibleStream == null) {
                //TODO should not happen, but can't assert that right now
                return NO_EVENT_VISIBLE_INFO;
            }

            List<User> people = userRepository.getPeople();
            List<User> watchesFromPeople = filterUsersWatchingEvent(people, wantedEventId);
            watchesFromPeople = sortWatchersListByJoinEventDate(watchesFromPeople);
            return buildEventInfo(visibleStream, watchesFromPeople, currentUser);
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

    private StreamInfo buildEventInfo(Stream currentVisibleStream, List<User> eventWatchers, User currentUser) {
        boolean isCurrentUserWatching = currentVisibleStream.getId().equals(currentUser.getIdWatchingEvent());
        return StreamInfo.builder()
          .event(currentVisibleStream)
          .watchers(eventWatchers)
          .currentUserWatching(isCurrentUserWatching ? currentUser : null)
          .build();
    }

    private StreamInfo noEvent() {
        return new StreamInfo();
    }

    private void notifyLoaded(final StreamInfo streamInfo) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(streamInfo);
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

        void onLoaded(StreamInfo streamInfo);
    }
}
