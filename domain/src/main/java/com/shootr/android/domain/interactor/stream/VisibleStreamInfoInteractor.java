package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class VisibleStreamInfoInteractor implements Interactor {

    private static final String VISIBLE_STREAM = null;
    private static final StreamInfo NO_STREAM_VISIBLE_INFO = null;

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final UserRepository localWatchRepository;
    private final StreamRepository remoteStreamRepository;
    private final StreamRepository localStreamRepository;
    private final SessionRepository sessionRepository;
    private ErrorCallback errorCallback;

    private String idStreamWanted;
    private Callback callback;

    @Inject public VisibleStreamInfoInteractor(InteractorHandler interactorHandler,
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
    public void obtainStreamInfo(String idStreamWanted, Callback callback, ErrorCallback errorCallback) {
        this.idStreamWanted = idStreamWanted;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    public void obtainVisibleStreamInfo(Callback callback, ErrorCallback errorCallback) {
        obtainStreamInfo(VISIBLE_STREAM, callback, errorCallback);
    }

    @Override public void execute() throws Exception {
        try {
            obtainLocalStreamInfo();
            obtainRemoteStreamInfo();
        }catch (ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    protected void obtainLocalStreamInfo() {
        StreamInfo streamInfo = getStreamInfo(localWatchRepository, localStreamRepository);
        if (streamInfo != NO_STREAM_VISIBLE_INFO) {
            notifyLoaded(streamInfo);
        }
    }

    protected void obtainRemoteStreamInfo() {
        StreamInfo streamInfo = getStreamInfo(remoteUserRepository, remoteStreamRepository);
        if (streamInfo != null) {
            notifyLoaded(streamInfo);
        } else {
            notifyLoaded(noStream());
        }
    }

    protected StreamInfo getStreamInfo(UserRepository userRepository, StreamRepository streamRepository) {
        User currentUser = userRepository.getUserById(sessionRepository.getCurrentUserId());

        String wantedStreamId = getWantedStreamId(currentUser);

        if (wantedStreamId != null) {
            Stream visibleStream = streamRepository.getStreamById(wantedStreamId);
            if (visibleStream == null) {
                //TODO should not happen, but can't assert that right now
                return NO_STREAM_VISIBLE_INFO;
            }

            List<User> people = userRepository.getPeople();
            List<User> watchesFromPeople = filterUsersWatchingStream(people, wantedStreamId);
            watchesFromPeople = sortWatchersListByJoinStreamDate(watchesFromPeople);
            return buildStreamInfo(visibleStream, watchesFromPeople, currentUser);
        }
        return NO_STREAM_VISIBLE_INFO;
    }

    private List<User> sortWatchersListByJoinStreamDate(List<User> watchesFromPeople) {
        Collections.sort(watchesFromPeople, new Comparator<User>() {
            @Override
            public int compare(User userModel, User t1) {
                return t1.getJoinStreamDate().compareTo(userModel.getJoinStreamDate());
            }
        });
        return watchesFromPeople;
    }

    private String getWantedStreamId(User currentUser) {
        if (idStreamWanted != null && !idStreamWanted.equals(VISIBLE_STREAM)) {
            return idStreamWanted;
        } else {
            return currentUser.getIdWatchingStream();
        }
    }

    protected List<User> filterUsersWatchingStream(List<User> people, String idStream) {
        List<User> watchers = new ArrayList<>();
        for (User user : people) {
            if (idStream.equals(user.getIdWatchingStream())) {
                watchers.add(user);
            }
        }
        return watchers;
    }

    private StreamInfo buildStreamInfo(Stream currentVisibleStream, List<User> streamWatchers, User currentUser) {
        boolean isCurrentUserWatching = currentVisibleStream.getId().equals(currentUser.getIdWatchingStream());
        return StreamInfo.builder()
          .stream(currentVisibleStream)
          .watchers(streamWatchers)
          .currentUserWatching(isCurrentUserWatching ? currentUser : null)
          .build();
    }

    private StreamInfo noStream() {
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
