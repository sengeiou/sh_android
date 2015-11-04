package com.shootr.mobile.domain.interactor.stream;

import java.util.List;
import javax.inject.Inject;

public class StreamSearchInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    public static final int MIN_SEARCH_LENGTH = 3;
    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.repository.StreamSearchRepository streamSearchRepository;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.utils.LocaleProvider localeProvider;

    private String query;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject
    public StreamSearchInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      @com.shootr.mobile.domain.repository.Remote
      com.shootr.mobile.domain.repository.StreamSearchRepository streamSearchRepository, com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
      com.shootr.mobile.domain.utils.LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.streamSearchRepository = streamSearchRepository;
        this.postExecutionThread = postExecutionThread;
        this.localeProvider = localeProvider;
    }

    public void searchStreams(String query, Callback callback, ErrorCallback errorCallback) {
        this.query = query;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        removeInvalidCharactersFromQuery();
        if (validateSearchQuery()) {
            try {
                performSearch();
            } catch (com.shootr.mobile.domain.exception.ServerCommunicationException networkError) {
                notifyError(networkError);
            }
        }
    }

    private void removeInvalidCharactersFromQuery() {
        query = query.replace("%", "").trim();
    }

    private boolean validateSearchQuery() {
        if (query == null || query.length() < MIN_SEARCH_LENGTH) {
            notifyError(new com.shootr.mobile.domain.exception.ShootrValidationException(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_SEARCH_TOO_SHORT));
            return false;
        }
        return true;
    }

    private void performSearch() {
        List<com.shootr.mobile.domain.StreamSearchResult> streams = streamSearchRepository.getStreams(query, localeProvider.getLocale());

        com.shootr.mobile.domain.StreamSearchResultList
          streamSearchResultList = new com.shootr.mobile.domain.StreamSearchResultList(streams);

        notifySearchResultsSuccessful(streamSearchResultList);
    }

    private void notifySearchResultsSuccessful(final com.shootr.mobile.domain.StreamSearchResultList streams) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(streams);
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

        void onLoaded(com.shootr.mobile.domain.StreamSearchResultList results);
    }
}
