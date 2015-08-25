package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamSearchRepository;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class GetCurrentUserListingStreamsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamSearchRepository localStreamSearchRepository;
    private final StreamSearchRepository remoteStreamSearchRepository;
    private final SessionRepository sessionRepository;

    private String idUser;
    private Callback<List<StreamSearchResult>> callback;

    @Inject public GetCurrentUserListingStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamSearchRepository localStreamRepositoty,
      @Remote StreamSearchRepository remoteStreamRepositoty, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamSearchRepository = localStreamRepositoty;
        this.remoteStreamSearchRepository = remoteStreamRepositoty;
        this.sessionRepository = sessionRepository;
    }

    public void loadCurrentUserListingStreams(Callback<List<StreamSearchResult>> callback){
        this.callback = callback;
        this.idUser = sessionRepository.getCurrentUserId();
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadCurrentUserListingStreamsFromLocal();
        loadCurrentUserListingStreamsFromRemote();
    }

    private void loadCurrentUserListingStreamsFromRemote() {
        try {
            loadUserListingStreamsFromRepository(remoteStreamSearchRepository);
        } catch (ShootrException error) {
            /* swallow error */
        }
    }

    private void loadCurrentUserListingStreamsFromLocal() {
        loadUserListingStreamsFromRepository(localStreamSearchRepository);
    }

    private void loadUserListingStreamsFromRepository(StreamSearchRepository streamSearchRepository){
        List<StreamSearchResult> listingStreams = streamSearchRepository.getStreamsListing(idUser);
        setWatchNumberInStreams(listingStreams);
        notifyLoaded(listingStreams);
    }

    private void setWatchNumberInStreams(List<StreamSearchResult> listingStreams) {
        Map<String, Integer> watchers = remoteStreamSearchRepository.getHolderWatchers();
        for (StreamSearchResult listingStream : listingStreams) {
            listingStream.setWatchersNumber(watchers.get(listingStream.getStream().getId()));
        }
    }

    private void notifyLoaded(final List<StreamSearchResult> listingStreams) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(listingStreams);
            }
        });
    }
}
