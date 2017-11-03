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

  public static final int FOLLOWERS = 0;
  public static final int CONNECTED = 1;

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalStreamRepository remoteStreamRepository;
  private final StreamRepository localStreamRepository;
  private String idStream;
  private Callback callback;
  private boolean localOnly;


  @Inject public WatchNumberInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ExternalStreamRepository remoteStreamRepository,
      @Local StreamRepository localStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteStreamRepository = remoteStreamRepository;
    this.localStreamRepository = localStreamRepository;
  }

  public void loadWatchersNumber(String idStream, boolean localOnly, Callback callback) {
    this.idStream = idStream;
    this.callback = callback;
    this.localOnly = localOnly;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    Long[] watchersCount = new Long[] { 0L, 0L };
    Stream stream = getRemoteStreamOrFallbackToLocal();
    if (stream != null) {
      watchersCount[FOLLOWERS] = stream.getTotalFavorites().longValue();
      watchersCount[CONNECTED] = stream.getTotalWatchers().longValue();
    }
    notifyLoaded(watchersCount);
  }

  private void notifyLoaded(final Long[] countIsWatching) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(countIsWatching);
      }
    });
  }

  private Stream getRemoteStreamOrFallbackToLocal() {
    if (localOnly) {
      return localStreamRepository.getStreamById(idStream, StreamMode.TYPES_STREAM);
    } else {
      try {
        return remoteStreamRepository.getStreamById(idStream, StreamMode.TYPES_STREAM);
      } catch (ServerCommunicationException networkError) {
        /* no-op */
      }
    }
    return null;
  }

  public interface Callback {

    void onLoaded(Long[] watchers);
  }
}
