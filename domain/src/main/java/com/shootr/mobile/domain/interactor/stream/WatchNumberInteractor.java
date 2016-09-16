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
import com.shootr.mobile.domain.repository.StreamRepository;
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

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final UserRepository remoteUserRepository;
  private final UserRepository localUserRepository;
  private final StreamRepository remoteStreamRepository;
  private final StreamRepository localStreamRepository;
  private String idStream;
  private Callback callback;

  @Inject public WatchNumberInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote UserRepository remoteUserRepository,
      @Local UserRepository localUserRepository, @Remote StreamRepository remoteStreamRepository,
      @Local StreamRepository localStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteUserRepository = remoteUserRepository;
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
    List<User> people = getRemotePeopleOrFallbackToLocal();
    Stream stream = getRemoteStreamOrFallbackToLocal();
    List<User> watchers = filterUsersWatchingStream(people, idStream);
    Integer[] watchersCount = setWatchers(stream, watchers);
    notifyLoaded(watchersCount);
  }

  private Integer[] setWatchers(Stream stream, List<User> watchers) {
    Integer[] watchersCount = new Integer[2];
    watchersCount[FRIENDS] = watchers.size();
    watchersCount[WATCHERS] =
        (stream.getWatchers() != null) ? stream.getWatchers().size() : NO_WATCHERS;
    return watchersCount;
  }

  protected List<User> filterUsersWatchingStream(List<User> people, String idStream) {
    List<User> watchers = new ArrayList<>();
    if (people != null) {
      for (User user : people) {
        if (idStream.equals(user.getIdWatchingStream())) {
          watchers.add(user);
        }
      }
    }
    return watchers;
  }

  private void notifyLoaded(final Integer[] countIsWatching) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(countIsWatching);
      }
    });
  }

  private List<User> getRemotePeopleOrFallbackToLocal() {
    List<User> people = localUserRepository.getPeople();
    if (people.isEmpty()) {
      try {
        people = remoteUserRepository.getPeople();
      } catch (ServerCommunicationException networkError) {
        /* no-op */
      }
    }
    return people;
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
