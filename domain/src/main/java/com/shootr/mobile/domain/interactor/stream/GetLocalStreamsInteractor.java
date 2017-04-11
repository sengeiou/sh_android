package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.repository.stream.InternalStreamSearchRepository;
import com.shootr.mobile.domain.repository.stream.RecentSearchRepository;
import com.shootr.mobile.domain.service.stream.WatchingStreamService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.List;
import javax.inject.Inject;

public class GetLocalStreamsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final InternalStreamSearchRepository streamSearchRepository;
  private final PostExecutionThread postExecutionThread;
  private final LocaleProvider localeProvider;
  private final WatchingStreamService watchingStreamService;
  private final RecentSearchRepository recentSearchRepository;

  private Interactor.Callback<List<Searchable>> callback;

  @Inject public GetLocalStreamsInteractor(InteractorHandler interactorHandler,
      InternalStreamSearchRepository streamSearchRepository, PostExecutionThread postExecutionThread,
      LocaleProvider localeProvider, WatchingStreamService watchingStreamService,
      RecentSearchRepository recentSearchRepository) {
    this.interactorHandler = interactorHandler;
    this.streamSearchRepository = streamSearchRepository;
    this.postExecutionThread = postExecutionThread;
    this.localeProvider = localeProvider;
    this.watchingStreamService = watchingStreamService;
    this.recentSearchRepository = recentSearchRepository;
  }

  public void loadStreams(Callback<List<Searchable>> callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadLocalStreams();
  }

  private void loadLocalStreams() {
    List<Searchable> searchables =
        recentSearchRepository.getDefaultSearch();
    //TODO: watchingStreamService.markWatchingStream(searchables);
    //StreamSearchResultList streamSearchResultList = new StreamSearchResultList(searchables);
    notifySearchResultsSuccessful(searchables);
  }

  private void notifySearchResultsSuccessful(final List<Searchable> searchables) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(searchables);
      }
    });
  }
}
