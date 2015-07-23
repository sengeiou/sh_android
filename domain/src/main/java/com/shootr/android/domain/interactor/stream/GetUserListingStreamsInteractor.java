package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.StreamSearchRepository;
import java.util.List;
import javax.inject.Inject;

public class GetUserListingStreamsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamSearchRepository localStreamSearchRepository;
    private final StreamSearchRepository remoteStreamSearchRepository;

    private String idUser;
    private Callback<List<StreamSearchResult>> callback;

    @Inject public GetUserListingStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamSearchRepository localStreamRepositoty,
      @Remote StreamSearchRepository remoteStreamRepositoty) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamSearchRepository = localStreamRepositoty;
        this.remoteStreamSearchRepository = remoteStreamRepositoty;
    }

    public void loadUserListingStreams(Callback<List<StreamSearchResult>> callback, String idUser){
        this.callback = callback;
        this.idUser = idUser;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadUserListingStreamsFromLocal();
        loadUserListingStreamsFromRemote();
    }

    private void loadUserListingStreamsFromRemote() {
        try {
            loadUserListingStreamsFromRepository(remoteStreamSearchRepository);
        } catch (ShootrException error) {
            /* swallow error */
        }
    }

    private void loadUserListingStreamsFromLocal() {
        loadUserListingStreamsFromRepository(localStreamSearchRepository);
    }

    private void loadUserListingStreamsFromRepository(StreamSearchRepository streamSearchRepository){
        List<StreamSearchResult> listingStreams = streamSearchRepository.getStreamsListing(idUser);
        notifyLoaded(listingStreams);
    }

    private void notifyLoaded(final List<StreamSearchResult> listingStreams) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(listingStreams);
            }
        });
    }
}
