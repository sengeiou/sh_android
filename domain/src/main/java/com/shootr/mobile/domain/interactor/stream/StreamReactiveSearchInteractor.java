package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.StreamSearchRepository;
import com.shootr.mobile.domain.service.stream.WatchingStreamService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.List;
import javax.inject.Inject;

public class StreamReactiveSearchInteractor implements Interactor {
  private final InteractorHandler interactorHandler;
  private final StreamSearchRepository remoteStreamSearchRepository;
  private final StreamSearchRepository localStreamSearchRepository;
  private final WatchingStreamService watchingStreamService;
  private final PostExecutionThread postExecutionThread;
  private final LocaleProvider localeProvider;

  private String query;
  private Callback<StreamSearchResultList> callback;
  private Interactor.ErrorCallback errorCallback;

  @Inject public StreamReactiveSearchInteractor(InteractorHandler interactorHandler,
      @Local StreamSearchRepository localStreamSearchRepository,
      @Remote StreamSearchRepository remoteStreamSearchRepository,
      WatchingStreamService watchingStreamService, PostExecutionThread postExecutionThread,
      LocaleProvider localeProvider) {
    this.interactorHandler = interactorHandler;
    this.remoteStreamSearchRepository = remoteStreamSearchRepository;
    this.localStreamSearchRepository = localStreamSearchRepository;
    this.watchingStreamService = watchingStreamService;
    this.postExecutionThread = postExecutionThread;
    this.localeProvider = localeProvider;
  }

  public void searchStreams(String query, Callback<StreamSearchResultList> callback,
      Interactor.ErrorCallback errorCallback) {
    this.query = query;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    removeInvalidCharactersFromQuery();
    loadLocalStreams();
    try {
      performSearch(remoteStreamSearchRepository);
    } catch (ServerCommunicationException networkError) {
      notifyError(networkError);
    }
  }

  private void loadLocalStreams() {
    List<StreamSearchResult> streams =
        localStreamSearchRepository.getStreams(query, localeProvider.getLocale(),
            StreamMode.TYPES_STREAM);
    if (streams.size() != 0) {
      StreamSearchResultList streamSearchResultList = new StreamSearchResultList(streams);

      notifySearchResultsSuccessful(streamSearchResultList);
    }
  }

  private void removeInvalidCharactersFromQuery() {
    query = query.replace("%", "").trim();
  }

  private void performSearch(StreamSearchRepository streamSearchRepository) {
    List<StreamSearchResult> streams =
        streamSearchRepository.getStreams(query, localeProvider.getLocale(),
            StreamMode.TYPES_STREAM);
    watchingStreamService.markWatchingStream(streams);

    StreamSearchResultList streamSearchResultList = new StreamSearchResultList(streams);

    notifySearchResultsSuccessful(streamSearchResultList);
  }

  private void notifySearchResultsSuccessful(final StreamSearchResultList streams) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(streams);
      }
    });
  }

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
