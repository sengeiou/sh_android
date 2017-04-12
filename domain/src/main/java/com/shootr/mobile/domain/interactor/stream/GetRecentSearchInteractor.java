package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.repository.stream.InternalStreamSearchRepository;
import com.shootr.mobile.domain.repository.stream.RecentSearchRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.List;
import javax.inject.Inject;

public class GetRecentSearchInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final InternalStreamSearchRepository streamSearchRepository;
  private final PostExecutionThread postExecutionThread;
  private final LocaleProvider localeProvider;
  private final RecentSearchRepository recentSearchRepository;

  private Interactor.Callback<List<Searchable>> callback;

  @Inject public GetRecentSearchInteractor(InteractorHandler interactorHandler,
      InternalStreamSearchRepository streamSearchRepository,
      PostExecutionThread postExecutionThread, LocaleProvider localeProvider,
      RecentSearchRepository recentSearchRepository) {
    this.interactorHandler = interactorHandler;
    this.streamSearchRepository = streamSearchRepository;
    this.postExecutionThread = postExecutionThread;
    this.localeProvider = localeProvider;
    this.recentSearchRepository = recentSearchRepository;
  }

  public void loadSearches(Callback<List<Searchable>> callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadLocalSearches();
  }

  private void loadLocalSearches() {
    List<Searchable> searchables = recentSearchRepository.getDefaultSearch();
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
