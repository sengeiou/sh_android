package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.ShootrValidationException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.StreamSearchRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import java.util.List;
import javax.inject.Inject;

public class StreamSearchInteractor implements Interactor {

    public static final int MIN_SEARCH_LENGTH = 3;
    private final InteractorHandler interactorHandler;
    private final StreamSearchRepository streamSearchRepository;
    private final PostExecutionThread postExecutionThread;
    private final LocaleProvider localeProvider;

    private String query;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject
    public StreamSearchInteractor(InteractorHandler interactorHandler,
      @Remote StreamSearchRepository streamSearchRepository, PostExecutionThread postExecutionThread,
      LocaleProvider localeProvider) {
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
            } catch (ServerCommunicationException networkError) {
                notifyError(networkError);
            }
        }
    }

    private void removeInvalidCharactersFromQuery() {
        query = query.replace("%", "").trim();
    }

    private boolean validateSearchQuery() {
        if (query == null || query.length() < MIN_SEARCH_LENGTH) {
            notifyError(new ShootrValidationException(ShootrError.ERROR_CODE_SEARCH_TOO_SHORT));
            return false;
        }
        return true;
    }

    private void performSearch() {
        List<StreamSearchResult> streams = streamSearchRepository.getStreams(query, localeProvider.getLocale());

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

    public interface Callback {

        void onLoaded(StreamSearchResultList results);
    }
}
