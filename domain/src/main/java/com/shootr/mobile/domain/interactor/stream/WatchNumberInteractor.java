package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Gives the number of people watching the stream the current user has visible.
 */
public class WatchNumberInteractor implements Interactor {

  public static final int NO_WATCHERS = 0;
  public static final int FRIENDS = 0;
  public static final int WATCHERS = 1;
  public static final int MAX_WATCHERS_VISIBLE = 50;

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository localUserRepository;
  private final UserRepository remoteUserRepository;
  private final ExternalStreamRepository remoteStreamRepository;
  private final StreamRepository localStreamRepository;
  private String idStream;
  private Callback callback;

  @Inject public WatchNumberInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository, ExternalStreamRepository remoteStreamRepository,
      @Local StreamRepository localStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.remoteUserRepository = remoteUserRepository;
    this.postExecutionThread = postExecutionThread;
    this.localUserRepository = localUserRepository;
    this.remoteStreamRepository = remoteStreamRepository;
    this.localStreamRepository = localStreamRepository;
  }

  public void loadWatchersNumber(String idStream, Callback callback) {
    this.idStream = idStream;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    remoteUserRepository.forceUpdatePeople();
    Stream stream = getRemoteStreamOrFallbackToLocal();
    List<User> watchers = new ArrayList<>();
    int followingNotCounted = 0;
    for (User user : localUserRepository.getLocalPeopleFromIdStream(idStream)) {
      if (user.isFollowing()) {
        watchers.add(user);
        if (!stream.getWatchers().contains(user)) {
          followingNotCounted++;
        }
      }
    }

    Integer[] watchersCount = setWatchers(stream, watchers, followingNotCounted);
    notifyLoaded(watchersCount);
  }

  private Integer[] setWatchers(Stream stream, List<User> watchers, int followingNotCounted) {
    Integer[] watchersCount = new Integer[2];
    watchersCount[FRIENDS] = watchers.size();
    if (watchers.size() >= MAX_WATCHERS_VISIBLE) {
      watchersCount[WATCHERS] =
          (stream.getTotalWatchers() != null) ? stream.getTotalWatchers() : NO_WATCHERS;
    } else {
      watchersCount[WATCHERS] =
          (stream.getWatchers() != null) ? stream.getWatchers().size() + followingNotCounted : NO_WATCHERS;
    }

    return watchersCount;
  }

  private void notifyLoaded(final Integer[] countIsWatching) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(countIsWatching);
      }
    });
  }

  private Stream getRemoteStreamOrFallbackToLocal() {
    try {
      return remoteStreamRepository.getStreamById(idStream, StreamMode.TYPES_STREAM);
    } catch (ServerCommunicationException networkError) {
      return localStreamRepository.getStreamById(idStream, StreamMode.TYPES_STREAM);
    }
  }

  public interface Callback {

    void onLoaded(Integer[] watchers);
  }
}
