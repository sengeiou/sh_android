package com.shootr.mobile.domain.interactor.discover;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.discover.DiscoverStream;
import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.discover.ExternalDiscoveredRepository;
import com.shootr.mobile.domain.repository.discover.InternalDiscoveredRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetDiscoverTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalDiscoveredRepository externalDiscoveredRepository;
  private final InternalDiscoveredRepository internalDiscoveredRepository;
  private final InternalFavoriteRepository localFavoriteRepository;
  private final LocaleProvider localeProvider;

  private Callback<DiscoverTimeline> callback;
  private ErrorCallback errorCallback;
  private boolean onlyFromLocal;

  @Inject public GetDiscoverTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      ExternalDiscoveredRepository externalDiscoveredRepository,
      InternalDiscoveredRepository internalDiscoveredRepository,
      InternalFavoriteRepository localFavoriteRepository, LocaleProvider localeProvider) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.externalDiscoveredRepository = externalDiscoveredRepository;
    this.internalDiscoveredRepository = internalDiscoveredRepository;
    this.localFavoriteRepository = localFavoriteRepository;
    this.localeProvider = localeProvider;
  }

  public void getDiscovered(boolean fromLocalOnly, Callback<DiscoverTimeline> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.onlyFromLocal = fromLocalOnly;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    List<Favorite> favorites = localFavoriteRepository.getFavorites();
    loadFromLocal(favorites);
    if (!onlyFromLocal) {
      loadFromRemote(favorites);
    }
  }

  private void loadFromRemote(List<Favorite> favorites) {
    try {
      DiscoverTimeline remoteDiscoverTimeline =
          externalDiscoveredRepository.getDiscoverTimeline(localeProvider.getLocale(),
              StreamMode.TYPES_STREAM);
      mapDiscovereds(remoteDiscoverTimeline.getDiscoverStreams(), favorites);
      internalDiscoveredRepository.putDiscoverTimeline(remoteDiscoverTimeline);
      notifyResult(remoteDiscoverTimeline);
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }
  }

  private void loadFromLocal(List<Favorite> favorites) {
    DiscoverTimeline localDiscoverTimeline =
        internalDiscoveredRepository.getDiscoverTimeline();
    if (localDiscoverTimeline != null && localDiscoverTimeline.getDiscoverStreams() != null) {
      mapDiscovereds(localDiscoverTimeline.getDiscoverStreams(), favorites);
      notifyResult(localDiscoverTimeline);
    }
  }

  private void mapDiscovereds(List<DiscoverStream> discoverStreams, List<Favorite> favorites) {
    List<String> favoriteStreamIds = new ArrayList<>(favorites.size());
    for (Favorite favorite : favorites) {
      favoriteStreamIds.add(favorite.getIdStream());
    }
    for (DiscoverStream discoverStream : discoverStreams) {
      if (null != discoverStream.getStream()) {
        discoverStream.setFavorite(favoriteStreamIds.contains(discoverStream.getStream().getId()));
      }
    }
  }

  private void notifyResult(final DiscoverTimeline discovereds) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(discovereds);
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
