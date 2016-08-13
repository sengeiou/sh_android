package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.RecentStreamRepository;
import com.shootr.mobile.domain.repository.StreamSearchRepository;
import com.shootr.mobile.domain.service.stream.WatchingStreamService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.List;
import javax.inject.Inject;

public class GetLocalStreamsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final StreamSearchRepository streamSearchRepository;
  private final PostExecutionThread postExecutionThread;
  private final LocaleProvider localeProvider;
  private final WatchingStreamService watchingStreamService;
  private final RecentStreamRepository recentStreamRepository;

  private Interactor.Callback<StreamSearchResultList> callback;

  @Inject public GetLocalStreamsInteractor(InteractorHandler interactorHandler,
      @Local StreamSearchRepository streamSearchRepository, PostExecutionThread postExecutionThread,
      LocaleProvider localeProvider, WatchingStreamService watchingStreamService,
      RecentStreamRepository recentStreamRepository) {
    this.interactorHandler = interactorHandler;
    this.streamSearchRepository = streamSearchRepository;
    this.postExecutionThread = postExecutionThread;
    this.localeProvider = localeProvider;
    this.watchingStreamService = watchingStreamService;
    this.recentStreamRepository = recentStreamRepository;
  }

  public void loadStreams(Callback<StreamSearchResultList> callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadLocalStreams();
  }

  private void loadLocalStreams() {
    List<StreamSearchResult> streams =
        recentStreamRepository.getDefaultStreams();
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
}
