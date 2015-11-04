package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.utils.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class GetStreamInfoInteractor implements Interactor {

    public static final int MAX_WATCHERS_VISIBLE = 50;
    public static final int MAX_WATCHERS_TO_SHOW = 25;
    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    private ErrorCallback errorCallback;

    private String idStreamWanted;
    private Callback callback;

    @Inject public GetStreamInfoInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      @Local UserRepository localUserRepository,
      @com.shootr.mobile.domain.repository.Remote UserRepository remoteUserRepository,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.StreamRepository remoteStreamRepository,
      @Local com.shootr.mobile.domain.repository.StreamRepository localStreamRepository,
      com.shootr.mobile.domain.repository.SessionRepository sessionRepository) {
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
        }catch (com.shootr.mobile.domain.exception.ServerCommunicationException networkError) {
            notifyError(networkError);
        }
    }

    protected void obtainLocalStreamInfo() {
        com.shootr.mobile.domain.StreamInfo streamInfo = getStreamInfo(localUserRepository, localStreamRepository, true);
        notifyLoaded(streamInfo);
    }

    protected void obtainRemoteStreamInfo() {
        com.shootr.mobile.domain.StreamInfo streamInfo = getStreamInfo(remoteUserRepository, remoteStreamRepository, false);
        if (streamInfo != null) {
            notifyLoaded(streamInfo);
        } else {
            notifyLoaded(noStream());
        }
    }

    protected com.shootr.mobile.domain.StreamInfo getStreamInfo(UserRepository userRepository,
      final com.shootr.mobile.domain.repository.StreamRepository streamRepository,
      boolean localOnly) {
        com.shootr.mobile.domain.User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        com.shootr.mobile.domain.Stream stream = streamRepository.getStreamById(idStreamWanted);
        checkNotNull(stream, new Preconditions.LazyErrorMessage() {
            @Override
            public Object getMessage() {
                return "Stream not found in "+streamRepository.getClass().getSimpleName();
            }
        });

        List<com.shootr.mobile.domain.User> people = userRepository.getPeople();
        List<com.shootr.mobile.domain.User> followingInStream = filterUsersWatchingStream(people, idStreamWanted);
        followingInStream = sortWatchersListByJoinStreamDate(followingInStream);

        Integer followingsNumber = followingInStream.size();

        List<com.shootr.mobile.domain.User> watchers = followingInStream;

        if (stream.getWatchers() != null) {
            List<com.shootr.mobile.domain.User> watchesFromStream = removeCurrentUserFromWatchers(stream.getWatchers());
            watchesFromStream.removeAll(followingInStream);
            watchesFromStream = sortWatchersListByJoinStreamDate(watchesFromStream);
            watchers.addAll(watchesFromStream);
        }

        Boolean hasMoreParticipants = false;
        if (watchers.size() > MAX_WATCHERS_VISIBLE) {
            watchers = watchers.subList(0, MAX_WATCHERS_TO_SHOW);
            hasMoreParticipants = true;
        }

        return buildStreamInfo(stream, watchers, currentUser, followingsNumber, hasMoreParticipants, localOnly);
    }

    private List<com.shootr.mobile.domain.User> sortWatchersListByJoinStreamDate(List<com.shootr.mobile.domain.User> watchesFromPeople) {
        Collections.sort(watchesFromPeople, new Comparator<com.shootr.mobile.domain.User>() {
            @Override public int compare(com.shootr.mobile.domain.User userModel, com.shootr.mobile.domain.User t1) {
                return t1.getJoinStreamDate().compareTo(userModel.getJoinStreamDate());
            }
        });
        return watchesFromPeople;
    }

    protected List<com.shootr.mobile.domain.User> filterUsersWatchingStream(List<com.shootr.mobile.domain.User> people, String idStream) {
        List<com.shootr.mobile.domain.User> watchers = new ArrayList<>();
        for (com.shootr.mobile.domain.User user : people) {
            if (idStream.equals(user.getIdWatchingStream())) {
                watchers.add(user);
            }
        }
        return watchers;
    }

    protected List<com.shootr.mobile.domain.User> removeCurrentUserFromWatchers(List<com.shootr.mobile.domain.User> watchers) {
        int meIndex = findMeIn(watchers);
        if (meIndex>=0) {
            watchers.remove(meIndex);
        }
        return watchers;
    }

    private int findMeIn(List<com.shootr.mobile.domain.User> watchers) {
        int meIndex = -1;
        for (int i=0; i<watchers.size(); i++) {
            if (watchers.get(i).getIdUser().equals(sessionRepository.getCurrentUserId())) {
                meIndex = i;
                break;
            }
        }
        return meIndex;
    }

    private com.shootr.mobile.domain.StreamInfo buildStreamInfo(
      com.shootr.mobile.domain.Stream stream, List<com.shootr.mobile.domain.User> streamWatchers, com.shootr.mobile.domain.User currentUser,
      Integer numberOfFollowing, Boolean hasMoreParticipants, boolean localOnly) {
        boolean isCurrentUserWatching = stream.getId().equals(currentUser.getIdWatchingStream());
        return com.shootr.mobile.domain.StreamInfo.builder()
          .stream(stream).watchers(streamWatchers)
          .currentUserWatching(isCurrentUserWatching ? currentUser : null).numberOfFollowing(numberOfFollowing)
          .hasMoreParticipants(hasMoreParticipants)
          .isDataComplete(!localOnly)
          .build();
    }

    private com.shootr.mobile.domain.StreamInfo noStream() {
        return new com.shootr.mobile.domain.StreamInfo();
    }

    private void notifyLoaded(final com.shootr.mobile.domain.StreamInfo streamInfo) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(streamInfo);
            }
        });
    }

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                errorCallback.onError(error);
            }
        });
    }

    public interface Callback {

        void onLoaded(com.shootr.mobile.domain.StreamInfo streamInfo);
    }
}
