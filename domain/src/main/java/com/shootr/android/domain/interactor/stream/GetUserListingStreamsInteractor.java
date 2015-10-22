package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.Listing;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.StreamSearchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetUserListingStreamsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final StreamSearchRepository localStreamSearchRepository;
    private final StreamSearchRepository remoteStreamSearchRepository;
    private final StreamRepository localStreamRepository;
    private final StreamRepository remoteStreamRepository;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;
    private final SessionRepository sessionRepository;

    private String idUser;
    private Callback<Listing> callback;
    private List<String> favoriteIds;
    private boolean isCurrentUser;

    @Inject
    public GetUserListingStreamsInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local StreamSearchRepository localStreamSearchRepository,
      @Remote StreamSearchRepository remoteStreamSearchRepository, @Local StreamRepository localStreamRepository,
      @Remote StreamRepository remoteStreamRepository, @Local FavoriteRepository localFavoriteRepository,
      @Remote FavoriteRepository remoteFavoriteRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamSearchRepository = localStreamSearchRepository;
        this.remoteStreamSearchRepository = remoteStreamSearchRepository;
        this.localStreamRepository = localStreamRepository;
        this.remoteStreamRepository = remoteStreamRepository;
        this.localFavoriteRepository = localFavoriteRepository;
        this.remoteFavoriteRepository = remoteFavoriteRepository;
        this.sessionRepository = sessionRepository;
    }

    public void loadUserListingStreams(Callback<Listing> callback, String idUser) {
        this.callback = callback;
        this.idUser = idUser;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        this.isCurrentUser = sessionRepository.getCurrentUserId().equals(idUser);
        loadUserListingStreamsFromLocal();
        loadUserListingStreamsFromRemote();
    }

    private void loadLocalFavoriteIds() {
        List<Favorite> favorites = localFavoriteRepository.getFavorites(idUser);
        favoriteIds = new ArrayList<>(favorites.size());
        for (Favorite favorite : favorites) {
            favoriteIds.add(favorite.getIdStream());
        }
    }

    private void loadRemoteFavoriteIds() {
        List<Favorite> favorites = remoteFavoriteRepository.getFavorites(idUser);
        favoriteIds = new ArrayList<>(favorites.size());
        for (Favorite favorite : favorites) {
            favoriteIds.add(favorite.getIdStream());
        }
    }

    private void loadUserListingStreamsFromRemote() {
        try {
            loadRemoteFavoriteIds();
            List<Stream> favoriteStreams = remoteStreamRepository.getStreamsByIds(favoriteIds);
            List<StreamSearchResult> holdingStreamResults =
              loadUserListingStreamsFromRepository(remoteStreamSearchRepository);

            Listing listing = getListing(favoriteStreams, holdingStreamResults);

            notifyLoaded(listing);
        } catch (ShootrException error) {
            /* swallow error */
        }
    }

    private void loadUserListingStreamsFromLocal() {
        loadLocalFavoriteIds();
        List<Stream> favoriteStreams = localStreamRepository.getStreamsByIds(favoriteIds);
        List<StreamSearchResult> holdingStreamResults =
          loadUserListingStreamsFromRepository(localStreamSearchRepository);
        Listing listing = getListing(favoriteStreams, holdingStreamResults);

        notifyLoaded(listing);
    }

    private Listing getListing(List<Stream> favoriteStreams, List<StreamSearchResult> streamSearchResults) {
        List<StreamSearchResult> favoriteStreamResults = getFavoriteStreamSearchResults(favoriteStreams);
        return Listing.builder()
          .holdingStreams(streamSearchResults)
          .favoritedStreams(favoriteStreamResults)
          .build();
    }

    private List<StreamSearchResult> getFavoriteStreamSearchResults(List<Stream> favoriteStreams) {
        List<StreamSearchResult> favoriteStreamResults = new ArrayList<>(favoriteStreams.size());
        for (Stream favoriteStream : favoriteStreams) {
            StreamSearchResult streamSearchResult = new StreamSearchResult();
            streamSearchResult.setStream(favoriteStream);
            favoriteStreamResults.add(streamSearchResult);
        }
        return favoriteStreamResults;
    }

    private List<StreamSearchResult> loadUserListingStreamsFromRepository(
      StreamSearchRepository streamSearchRepository) {
        List<StreamSearchResult> listingStreams = streamSearchRepository.getStreamsListing(idUser);
        return getNotRemovedStreams(listingStreams);
    }

    private List<StreamSearchResult> getNotRemovedStreams(List<StreamSearchResult> listingStreams) {
        List<StreamSearchResult> listing = new ArrayList<>();
        for (StreamSearchResult listingStream : listingStreams) {
            if (!listingStream.getStream().isRemoved()) {
                listing.add(listingStream);
            }
        }
        return listing;
    }

    private void notifyLoaded(final Listing listingStreams) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(listingStreams);
            }
        });
    }
}
