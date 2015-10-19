package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Listing;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamSearchRepository;
import java.util.ArrayList;
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
    private Callback<Listing> callback;
    private ErrorCallback errorCallback;

    @Inject public GetCurrentUserListingStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamSearchRepository localStreamRepositoty,
      @Remote StreamSearchRepository remoteStreamRepositoty, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamSearchRepository = localStreamRepositoty;
        this.remoteStreamSearchRepository = remoteStreamRepositoty;
        this.sessionRepository = sessionRepository;
    }

    public void loadCurrentUserListingStreams(Callback<Listing> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.idUser = sessionRepository.getCurrentUserId();
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            loadCurrentUserListingStreamsFromLocal();
            loadCurrentUserListingStreamsFromRemote();
        } catch (ServerCommunicationException error) {
            notifyError(error);
        }
    }

    private void loadCurrentUserListingStreamsFromRemote() {
        loadUserListingStreamsFromRepository(remoteStreamSearchRepository);
    }

    private void loadCurrentUserListingStreamsFromLocal() {
        loadUserListingStreamsFromRepository(localStreamSearchRepository);
    }

    private void loadUserListingStreamsFromRepository(StreamSearchRepository streamSearchRepository) {
        List<StreamSearchResult> listingStreams = streamSearchRepository.getStreamsListing(idUser);
        setFavoritesNumberInStreams(listingStreams);
        List<StreamSearchResult> listingWithoutRemoved = retainStreamsNotRemoved(listingStreams);

        notifyLoaded(Listing.builder()
            .holdingStreams(listingWithoutRemoved)
            .build());
    }

    private List<StreamSearchResult> retainStreamsNotRemoved(List<StreamSearchResult> listingStreams) {
        List<StreamSearchResult> listing = new ArrayList<>();
        for (StreamSearchResult listingStream : listingStreams) {
            if (!listingStream.getStream().isRemoved()) {
                listing.add(listingStream);
            }
        }
        return listing;
    }

    private void setFavoritesNumberInStreams(List<StreamSearchResult> listingStreams) {
        Map<String, Integer> favorites = remoteStreamSearchRepository.getHolderFavorites();
        for (StreamSearchResult listingStream : listingStreams) {
            listingStream.setWatchersNumber(favorites.get(listingStream.getStream().getId()));
        }
    }

    private void notifyLoaded(final Listing listingStreams) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(listingStreams);
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
