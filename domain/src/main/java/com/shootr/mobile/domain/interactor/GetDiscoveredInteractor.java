package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.Discovered;
import com.shootr.mobile.domain.DiscoveredType;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.discover.ExternalDiscoveredRepository;
import com.shootr.mobile.domain.repository.discover.InternalDiscoveredRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetDiscoveredInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalDiscoveredRepository internalDiscoveredRepository;
  private final ExternalDiscoveredRepository externalDiscoveredRepository;
  private final InternalFavoriteRepository localFavoriteRepository;
  private final LocaleProvider localeProvider;

  private Callback<List<Discovered>> callback;
  private ErrorCallback errorCallback;

  @Inject public GetDiscoveredInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      InternalDiscoveredRepository internalDiscoveredRepository,
      ExternalDiscoveredRepository externalDiscoveredRepository,
      InternalFavoriteRepository localFavoriteRepository, LocaleProvider localeProvider) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.internalDiscoveredRepository = internalDiscoveredRepository;
    this.externalDiscoveredRepository = externalDiscoveredRepository;
    this.localFavoriteRepository = localFavoriteRepository;
    this.localeProvider = localeProvider;
  }

  public void getDiscovered(Callback<List<Discovered>> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    List<Discovered> localeDiscovereds = internalDiscoveredRepository.getDiscovered();
    List<Favorite> favorites = localFavoriteRepository.getFavorites();
    mapDiscovereds(localeDiscovereds, favorites);
    notifyResult(localeDiscovereds);
    try {
      List<Discovered> remoteDiscovereds =
          externalDiscoveredRepository.getDiscovered(localeProvider.getLocale(),
              StreamMode.TYPES_STREAM, DiscoveredType.TYPES_STREAM);
      mapDiscovereds(remoteDiscovereds, favorites);
      notifyResult(remoteDiscovereds);
    } catch (ServerCommunicationException error) {
      notifyError(error);
    }

  }

  private void mapDiscovereds(List<Discovered> discovereds, List<Favorite> favorites) {
    List<String> favoriteStreamIds = new ArrayList<>(favorites.size());
    for (Favorite favorite : favorites) {
      favoriteStreamIds.add(favorite.getIdStream());
    }
    for (Discovered discovered : discovereds) {
      discovered.setFaved(favoriteStreamIds.contains(discovered.getStream().getId()));
    }
  }

  private void notifyResult(final List<Discovered> discovereds) {
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
