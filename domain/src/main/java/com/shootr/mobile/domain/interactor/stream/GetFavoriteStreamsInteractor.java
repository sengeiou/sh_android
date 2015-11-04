package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Favorite;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.repository.FavoriteRepository;
import com.shootr.mobile.domain.repository.Local;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class GetFavoriteStreamsInteractor implements Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;
    private final com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    private final com.shootr.mobile.domain.repository.WatchersRepository watchersRepository;
    private final com.shootr.mobile.domain.repository.SessionRepository sessionRepository;

    private Callback<List<StreamSearchResult>> callback;
    private boolean loadLocalOnly = false;

    @Inject public GetFavoriteStreamsInteractor(com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local FavoriteRepository localFavoriteRepository,
      @com.shootr.mobile.domain.repository.Remote FavoriteRepository remoteFavoriteRepository, @Local
    com.shootr.mobile.domain.repository.StreamRepository localStreamRepository,
      @Local com.shootr.mobile.domain.repository.WatchersRepository watchersRepository, com.shootr.mobile.domain.repository.SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFavoriteRepository = localFavoriteRepository;
        this.remoteFavoriteRepository = remoteFavoriteRepository;
        this.localStreamRepository = localStreamRepository;
        this.watchersRepository = watchersRepository;
        this.sessionRepository = sessionRepository;
    }

    public void loadFavoriteStreams(Interactor.Callback<List<StreamSearchResult>> callback) {
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
        loadFavoritesFrom(localFavoriteRepository);
    }

    private void loadRemoteFavorites() {
        try {
            loadFavoritesFrom(remoteFavoriteRepository);
        } catch (com.shootr.mobile.domain.exception.ServerCommunicationException networkError) {
            /* no-op */
        }
    }

    private void loadFavoritesFrom(FavoriteRepository favoriteRepository) {
        List<Favorite> favorites = favoriteRepository.getFavorites(sessionRepository.getCurrentUserId());
        List<com.shootr.mobile.domain.Stream> favoriteStreams = streamsFromFavorites(favorites);
        favoriteStreams = sortStreamsByName(favoriteStreams);
        List<StreamSearchResult> favoriteStreamsWithWatchers = addWatchersToStreams(favoriteStreams);
        markWatchingStream(favoriteStreamsWithWatchers);
        notifyLoaded(favoriteStreamsWithWatchers);
    }

    private void markWatchingStream(List<StreamSearchResult> streams) {
        String watchingId = null;
        User currentUser = sessionRepository.getCurrentUser();
        if (currentUser != null) {
            watchingId = sessionRepository.getCurrentUser().getIdWatchingStream();
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

    private List<com.shootr.mobile.domain.Stream> sortStreamsByName(List<com.shootr.mobile.domain.Stream> streams) {
        Collections.sort(streams, new com.shootr.mobile.domain.Stream.StreamNameComparator());
        return streams;
    }

    private List<com.shootr.mobile.domain.Stream> streamsFromFavorites(List<Favorite> favorites) {
        List<String> idStreams = new ArrayList<>();
        for (Favorite favorite : favorites) {
            idStreams.add(favorite.getIdStream());
        }
        return localStreamRepository.getStreamsByIds(idStreams);
    }

    private List<StreamSearchResult> addWatchersToStreams(List<com.shootr.mobile.domain.Stream> streams) {
        Map<String, Integer> watchersInStreams = watchersRepository.getWatchers();
        List<StreamSearchResult> streamsWithWatchers = new ArrayList<>(streams.size());
        for (com.shootr.mobile.domain.Stream stream : streams) {
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
