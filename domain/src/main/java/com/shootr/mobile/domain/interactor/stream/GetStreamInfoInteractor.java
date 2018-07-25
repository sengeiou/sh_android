package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.StreamRemovedException;
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
import java.util.ArrayList;
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
    if (stream == null) {
      notifyError(new StreamRemovedException(new Throwable()));
    }
    List<User> watchers = new ArrayList<>();
    boolean hasMoreParticipants = false;
    if (!localOnly) {
      watchers.addAll(stream.getWatchers());
      removeCurrentUserFromWatchers(watchers);
      watchers.add(0, currentUser);
      if (watchers.size() >= MAX_WATCHERS_VISIBLE) {
        watchers = watchers.subList(0, MAX_WATCHERS_TO_SHOW);
        hasMoreParticipants = true;
      }
    } else {
      watchers.add(currentUser);
    }

    return buildStreamInfo(stream, watchers, currentUser, stream.getTotalFollowingWatchers(),
        hasMoreParticipants, localOnly);
  }

  private List<User> removeCurrentUserFromWatchers(List<User> watchers) {
    int meIndex = findMeIn(watchers);
    if (meIndex >= 0) {
      watchers.remove(meIndex);
    }
    return watchers;
  }

  private int findMeIn(List<User> watchers) {
    int meIndex = -1;
    for (int i = 0; i < watchers.size(); i++) {
      if (watchers.get(i).getIdUser().equals(sessionRepository.getCurrentUserId())) {
        meIndex = i;
        break;
      }
    }
    return meIndex;
  }

  private StreamInfo buildStreamInfo(Stream stream, List<User> streamWatchers, User currentUser,
      Integer numberOfFollowing, Boolean hasMoreParticipants, boolean localOnly) {
    boolean isCurrentUserWatching = stream.getId().equals(currentUser.getIdWatchingStream());
    return StreamInfo.builder()
        .stream(stream)
        .watchers(streamWatchers)
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
