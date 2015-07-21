package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class GetUserListingEventsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamSearchRepository localStreamSearchRepository;
    private final StreamSearchRepository remoteStreamSearchRepository;

    private String idUser;
    private Callback<List<EventSearchResult>> callback;

    @Inject public GetUserListingEventsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamSearchRepository localEventRepositoty,
      @Remote StreamSearchRepository remoteEventRepositoty) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamSearchRepository = localEventRepositoty;
        this.remoteStreamSearchRepository = remoteEventRepositoty;
    }

    public void loadUserListingEvents(Callback<List<EventSearchResult>> callback, String idUser){
        this.callback = callback;
        this.idUser = idUser;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadUserListingEventsFromLocal();
        loadUserListingEventsFromRemote();
    }

    private void loadUserListingEventsFromRemote() {
        try {
            loadUserListingEventsFromRepository(remoteStreamSearchRepository);
        } catch (ShootrException error) {
            /* swallow error */
        }
    }

    private void loadUserListingEventsFromLocal() {
        loadUserListingEventsFromRepository(localStreamSearchRepository);
    }

    private void loadUserListingEventsFromRepository(StreamSearchRepository eventRepository){
        List<EventSearchResult> listingEvents = eventRepository.getStreamsListing(idUser);
        notifyLoaded(listingEvents);
    }

    private void notifyLoaded(final List<EventSearchResult> listingEvents) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(listingEvents);
            }
        });
    }
}
