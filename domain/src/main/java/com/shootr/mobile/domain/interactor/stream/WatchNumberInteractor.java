package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

/**
 * Gives the number of people watching the stream the current user has visible.
 */
public class WatchNumberInteractor implements Interactor {

  public static final int FRIENDS = 0;
  public static final int WATCHERS = 1;

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalStreamRepository remoteStreamRepository;
  private final StreamRepository localStreamRepository;
  private String idStream;
  private Callback callback;

  @Inject public WatchNumberInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ExternalStreamRepository remoteStreamRepository,
      @Local StreamRepository localStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteStreamRepository = remoteStreamRepository;
    this.localStreamRepository = localStreamRepository;
  }

  public void loadWatchersNumber(String idStream, Callback callback) {
    this.idStream = idStream;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    Stream stream = getRemoteStreamOrFallbackToLocal();
    Integer[] watchersCount = new Integer[] { 0, 0 };
    watchersCount[FRIENDS] = stream.getTotalFollowingWatchers();
    watchersCount[WATCHERS] = stream.getTotalWatchers();
    notifyLoaded(watchersCount);
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
