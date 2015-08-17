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
import com.shootr.android.domain.utils.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class GetStreamInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final StreamRepository remoteStreamRepository;
    private final StreamRepository localStreamRepository;
    private final SessionRepository sessionRepository;
    private ErrorCallback errorCallback;

    private String idStreamWanted;
    private Callback callback;

    @Inject public GetStreamInfoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local UserRepository localUserRepository,
      @Remote UserRepository remoteUserRepository,
      @Remote StreamRepository remoteStreamRepository,
      @Local StreamRepository localStreamRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localStreamRepository = localStreamRepository;
        this.sessionRepository = sessionRepository;
    }

    public void obtainStreamInfo(String idStreamWanted, Callback callback, ErrorCallback errorCallback) {
        this.idStreamWanted = checkNotNull(idStreamWanted);;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
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
        StreamInfo streamInfo = getStreamInfo(localUserRepository, localStreamRepository);
        notifyLoaded(streamInfo);
    }

    protected void obtainRemoteStreamInfo() {
        StreamInfo streamInfo = getStreamInfo(remoteUserRepository, remoteStreamRepository);
        if (streamInfo != null) {
            notifyLoaded(streamInfo);
        } else {
            notifyLoaded(noStream());
        }
    }

    protected StreamInfo getStreamInfo(UserRepository userRepository, final StreamRepository streamRepository) {
        User currentUser = userRepository.getUserById(sessionRepository.getCurrentUserId());
        Stream stream = streamRepository.getStreamById(idStreamWanted);
        checkNotNull(stream, new Preconditions.LazyErrorMessage() {
            @Override
            public Object getMessage() {
                return "Stream not found in "+streamRepository.getClass().getSimpleName();
            }
        });

        List<User> people = userRepository.getPeople();
        List<User> watchesFromPeople = filterUsersWatchingStream(people, idStreamWanted);
        watchesFromPeople = sortWatchersListByJoinStreamDate(watchesFromPeople);
        return buildStreamInfo(stream, watchesFromPeople, currentUser);
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

    protected List<User> filterUsersWatchingStream(List<User> people, String idStream) {
        List<User> watchers = new ArrayList<>();
        for (User user : people) {
            if (idStream.equals(user.getIdWatchingStream())) {
                watchers.add(user);
            }
        }
        return watchers;
    }

    private StreamInfo buildStreamInfo(Stream stream, List<User> streamWatchers, User currentUser) {
        boolean isCurrentUserWatching = stream.getId().equals(currentUser.getIdWatchingStream());
        return StreamInfo.builder()
          .stream(stream)
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
