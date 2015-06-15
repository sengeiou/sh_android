package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetFavoriteEventsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;

    private Callback<List<EventSearchResult>> callback;

    @Inject public GetFavoriteEventsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }

    public void loadFavoriteEvents(Interactor.Callback<List<EventSearchResult>> callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        notifyLoaded(new ArrayList<EventSearchResult>());
    }

    private void notifyLoaded(final List<EventSearchResult> eventSearchResults) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(eventSearchResults);
            }
        });
    }
}
