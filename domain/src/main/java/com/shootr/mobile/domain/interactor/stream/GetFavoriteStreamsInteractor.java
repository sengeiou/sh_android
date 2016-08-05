package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.repository.WatchersRepository;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class GetFavoriteStreamsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalFavoriteRepository localFavoriteRepository;
  private final ExternalFavoriteRepository remoteFavoriteRepository;
  private final StreamRepository localStreamRepository;
  private final WatchersRepository watchersRepository;
  private final SessionRepository sessionRepository;
  private final UserRepository localUserRepository;

  private Callback<List<StreamSearchResult>> callback;
  private boolean loadLocalOnly = false;

  @Inject public GetFavoriteStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalFavoriteRepository localFavoriteRepository,
      ExternalFavoriteRepository remoteFavoriteRepository,
      @Local StreamRepository localStreamRepository, @Local WatchersRepository watchersRepository,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localFavoriteRepository = localFavoriteRepository;
    this.remoteFavoriteRepository = remoteFavoriteRepository;
    this.localStreamRepository = localStreamRepository;
    this.watchersRepository = watchersRepository;
    this.sessionRepository = sessionRepository;
    this.localUserRepository = localUserRepository;
  }

  public void loadFavoriteStreams(Callback<List<StreamSearchResult>> callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  public void loadFavoriteStreamsFromLocalOnly(Callback<List<StreamSearchResult>> callback) {
    loadLocalOnly = true;
    this.loadFavoriteStreams(callback);
  }

  @Override public void execute() throws Exception {
    loadLocalFavorites();
    if (!loadLocalOnly) {
      loadRemoteFavorites();
    }
  }

  private void loadLocalFavorites() {
    List<Favorite> favorites = localFavoriteRepository.getFavorites();
    mapFavorites(favorites);
  }

  private void loadRemoteFavorites() {
    try {
      List<Favorite> favorites =
          remoteFavoriteRepository.getFavorites(sessionRepository.getCurrentUserId());
      mapFavorites(favorites);
    } catch (ServerCommunicationException networkError) {
            /* no-op */
    }
  }

  private void mapFavorites(List<Favorite> favorites) {
    List<Stream> favoriteStreams = streamsFromFavorites(favorites);
    favoriteStreams = sortStreamsByName(favoriteStreams);
    List<StreamSearchResult> favoriteStreamsWithWatchers = addWatchersToStreams(favoriteStreams);
    markWatchingStream(favoriteStreamsWithWatchers);
    notifyLoaded(favoriteStreamsWithWatchers);
  }

  private void markWatchingStream(List<StreamSearchResult> streams) {
    String watchingId = null;
    User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    if (currentUser != null) {
      watchingId = localUserRepository.getUserById(sessionRepository.getCurrentUserId())
          .getIdWatchingStream();
    }

    if (watchingId == null) {
      return;
    }

    for (StreamSearchResult stream : streams) {
      if (stream.getStream().getId().equals(watchingId)) {
        stream.setIsWatching(true);
      }
    }
  }

  private List<Stream> sortStreamsByName(List<Stream> streams) {
    Collections.sort(streams, new Stream.StreamNameComparator());
    return streams;
  }

  private List<Stream> streamsFromFavorites(List<Favorite> favorites) {
    List<String> idStreams = new ArrayList<>();
    for (Favorite favorite : favorites) {
      idStreams.add(favorite.getIdStream());
    }
    return localStreamRepository.getStreamsByIds(idStreams, StreamMode.TYPES_STREAM);
  }

  private List<StreamSearchResult> addWatchersToStreams(List<Stream> streams) {
    Map<String, Integer> watchersInStreams = watchersRepository.getWatchers();
    List<StreamSearchResult> streamsWithWatchers = new ArrayList<>(streams.size());
    for (Stream stream : streams) {
      Integer streamsWatchers = watchersInStreams.get(stream.getId());
      streamsWithWatchers.add(new StreamSearchResult(stream, streamsWatchers));
    }
    return streamsWithWatchers;
  }

  private void notifyLoaded(final List<StreamSearchResult> streamSearchResults) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(streamSearchResults);
      }
    });
  }
}
