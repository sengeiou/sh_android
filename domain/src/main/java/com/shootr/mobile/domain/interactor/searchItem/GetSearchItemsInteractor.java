package com.shootr.mobile.domain.interactor.searchItem;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.repository.searchItem.ExternalSearchItemRepository;
import java.util.List;
import javax.inject.Inject;

public class GetSearchItemsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalSearchItemRepository externalSearchItemRepository;
  private final ErrorCallback errorCallback;
  private String query;
  private Interactor.Callback<List<Searchable>> callback;

  @Inject public GetSearchItemsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ExternalSearchItemRepository externalSearchItemRepository, ErrorCallback errorCallback) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.externalSearchItemRepository = externalSearchItemRepository;
    this.errorCallback = errorCallback;
  }

  public void searchItems(String query, Callback<List<Searchable>> callback) {
    this.query = query;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      List<Searchable> searchables = externalSearchItemRepository.getSearch(query, SearchableType.SEARCHABLE_TYPES);
      notifyResult(searchables);
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void notifyResult(final List<Searchable> searchables) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(searchables);
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
