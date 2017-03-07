package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.Listing;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.InternalStreamSearchRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamSearchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetUserListingStreamsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalStreamSearchRepository localStreamSearchRepository;
  private final StreamSearchRepository remoteStreamSearchRepository;
  private final StreamRepository localStreamRepository;
  private final ExternalStreamRepository remoteStreamRepository;
  private final ExternalFavoriteRepository remoteFavoriteRepository;

  private String idUser;
  private Callback<Listing> callback;
  private ErrorCallback errorCallback;
  private List<String> favoriteIds;

  @Inject public GetUserListingStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      InternalStreamSearchRepository localStreamSearchRepository,
      @Remote StreamSearchRepository remoteStreamSearchRepository,
      @Local StreamRepository localStreamRepository,
      ExternalStreamRepository remoteStreamRepository,
      ExternalFavoriteRepository remoteFavoriteRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamSearchRepository = localStreamSearchRepository;
    this.remoteStreamSearchRepository = remoteStreamSearchRepository;
    this.localStreamRepository = localStreamRepository;
    this.remoteStreamRepository = remoteStreamRepository;
    this.remoteFavoriteRepository = remoteFavoriteRepository;
  }

  public void loadUserListingStreams(Callback<Listing> callback, ErrorCallback errorCallback,
      String idUser) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.idUser = idUser;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      loadRemoteFavoriteIds();
      loadUserListingStreamsFromLocal();
      loadUserListingStreamsFromRemote();
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void loadRemoteFavoriteIds() {
    List<Favorite> favorites = remoteFavoriteRepository.getFavorites(idUser);
    favoriteIds = new ArrayList<>(favorites.size());
    for (Favorite favorite : favorites) {
      favoriteIds.add(favorite.getIdStream());
    }
  }

  private void loadUserListingStreamsFromRemote() throws ServerCommunicationException {
    try {
      List<Stream> favoriteStreams = new ArrayList<>();
      if (!favoriteIds.isEmpty()) {
        favoriteStreams =
            remoteStreamRepository.getStreamsByIds(favoriteIds, StreamMode.TYPES_STREAM);
      }
      List<StreamSearchResult> holdingStreamResults =
          loadUserListingStreamsFromRepository(remoteStreamSearchRepository);

      Listing listing = getListing(favoriteStreams, holdingStreamResults);

      notifyLoaded(listing);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void loadUserListingStreamsFromLocal() {
    List<Stream> favoriteStreams =
        localStreamRepository.getStreamsByIds(favoriteIds, StreamMode.TYPES_STREAM);
    List<StreamSearchResult> holdingStreamResults =
        loadUserListingStreamsFromRepository(localStreamSearchRepository);
    Listing listing = getListing(favoriteStreams, holdingStreamResults);

    notifyLoaded(listing);
  }

  private Listing getListing(List<Stream> favoriteStreams,
      List<StreamSearchResult> streamSearchResults) {
    List<StreamSearchResult> favoriteStreamResults =
        getFavoriteStreamSearchResults(favoriteStreams);
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
    List<StreamSearchResult> listingStreams =
        streamSearchRepository.getStreamsListing(idUser, StreamMode.TYPES_STREAM);
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

  private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}