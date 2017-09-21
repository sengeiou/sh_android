package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.Followable;
import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.Follows;
import com.shootr.mobile.domain.model.stream.Listing;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import com.shootr.mobile.domain.repository.stream.InternalStreamSearchRepository;
import com.shootr.mobile.domain.repository.stream.StreamSearchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetUserListingStreamsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalStreamSearchRepository localStreamSearchRepository;
  private final StreamSearchRepository remoteStreamSearchRepository;
  private final FollowRepository remoteFollowRepository;

  private String idUser;
  private Callback<Listing> callback;
  private ErrorCallback errorCallback;
  private List<Stream> favoriteStreams;

  @Inject public GetUserListingStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      InternalStreamSearchRepository localStreamSearchRepository,
      @Remote StreamSearchRepository remoteStreamSearchRepository,
      @Remote FollowRepository remoteFollowRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localStreamSearchRepository = localStreamSearchRepository;
    this.remoteStreamSearchRepository = remoteStreamSearchRepository;
    this.remoteFollowRepository = remoteFollowRepository;
  }

  public void loadUserListingStreams(Callback<Listing> callback, ErrorCallback errorCallback,
      String idUser) {
    favoriteStreams = new ArrayList<>();
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
    Follows following =
        remoteFollowRepository.getFollowing(idUser, new String[] { FollowableType.STREAM }, null);
    for (Followable follow : following.getData()) {
      favoriteStreams.add((Stream) follow);
    }
  }

  private void loadUserListingStreamsFromRemote() throws ServerCommunicationException {
    try {
      List<StreamSearchResult> holdingStreamResults =
          loadUserListingStreamsFromRepository(remoteStreamSearchRepository);

      Listing listing = getListing(favoriteStreams, holdingStreamResults);

      notifyLoaded(listing);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void loadUserListingStreamsFromLocal() {
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