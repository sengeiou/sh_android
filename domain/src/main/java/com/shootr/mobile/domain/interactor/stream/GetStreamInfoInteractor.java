package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamInfo;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class GetStreamInfoInteractor implements Interactor {

  public static final int MAX_WATCHERS_VISIBLE = 50;
  public static final int MAX_WATCHERS_TO_SHOW = 50;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository localUserRepository;
  private final ExternalStreamRepository remoteStreamRepository;
  private final StreamRepository localStreamRepository;
  private final SessionRepository sessionRepository;
  private ErrorCallback errorCallback;

  private String idStreamWanted;
  private Callback callback;

  @Inject public GetStreamInfoInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local UserRepository localUserRepository,
      ExternalStreamRepository remoteStreamRepository,
      @Local StreamRepository localStreamRepository, SessionRepository sessionRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localUserRepository = localUserRepository;
    this.remoteStreamRepository = remoteStreamRepository;
    this.localStreamRepository = localStreamRepository;
    this.sessionRepository = sessionRepository;
  }

  public void obtainStreamInfo(String idStreamWanted, Callback callback,
      ErrorCallback errorCallback) {
    this.idStreamWanted = checkNotNull(idStreamWanted);
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      obtainLocalStreamInfo();
      obtainRemoteStreamInfo();
    } catch (ServerCommunicationException networkError) {
      notifyError(networkError);
    }
  }

  private void obtainLocalStreamInfo() {
    StreamInfo streamInfo = getStreamInfo(localStreamRepository, true);
    notifyLoaded(streamInfo);
  }

  private void obtainRemoteStreamInfo() {
    StreamInfo streamInfo = getStreamInfo(remoteStreamRepository, false);
    if (streamInfo != null) {
      notifyLoaded(streamInfo);
    }
  }

  protected StreamInfo getStreamInfo(final StreamRepository streamRepository, boolean localOnly) {
    User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    Stream stream = streamRepository.getStreamById(idStreamWanted, StreamMode.TYPES_STREAM);
    checkNotNull(stream, new Preconditions.LazyErrorMessage() {
      @Override public Object getMessage() {
        return "Stream not found in " + streamRepository.getClass().getSimpleName();
      }
    });

    List<User> watchers = new ArrayList<>();
    watchers.addAll(stream.getWatchers());
    setupFollowing(watchers);
    sortWatchersListByJoinStreamDate(watchers);

    boolean hasMoreParticipants = false;
    if (watchers.size() >= MAX_WATCHERS_VISIBLE) {
      watchers = watchers.subList(0, MAX_WATCHERS_TO_SHOW);
      hasMoreParticipants = true;
    }

    return buildStreamInfo(stream, watchers, currentUser, stream.getTotalFollowingWatchers(), hasMoreParticipants,
        localOnly);
  }

  private void setupFollowing(List<User> watchers) {
    for (User watcher : watchers) {
      watcher.setFollowing(localUserRepository.isFollowing(watcher.getIdUser()));
      watcher.setMe(sessionRepository.getCurrentUserId().equals(watcher.getIdUser()));
    }
  }

  private List<User> sortWatchersListByJoinStreamDate(List<User> watchesFromPeople) {
    Collections.sort(watchesFromPeople, new Comparator<User>() {
      @Override public int compare(User userModel, User t1) {
        return t1.getJoinStreamDate().compareTo(userModel.getJoinStreamDate());
      }
    });
    return watchesFromPeople;
  }

  private StreamInfo buildStreamInfo(Stream stream, List<User> streamWatchers, User currentUser,
      Integer numberOfFollowing, Boolean hasMoreParticipants, boolean localOnly) {
    boolean isCurrentUserWatching = stream.getId().equals(currentUser.getIdWatchingStream());
    return StreamInfo.builder()
        .stream(stream)
        .watchers(stream.getWatchers())
        .currentUserWatching(isCurrentUserWatching ? currentUser : null)
        .numberOfFollowing(numberOfFollowing)
        .hasMoreParticipants(hasMoreParticipants)
        .isDataComplete(!localOnly)
        .build();
  }

  private void notifyLoaded(final StreamInfo streamInfo) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(streamInfo);
      }
    });
  }

  private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }

  public interface Callback {

    void onLoaded(StreamInfo streamInfo);
  }
}
