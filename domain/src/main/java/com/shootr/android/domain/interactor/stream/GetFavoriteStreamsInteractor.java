package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class GetFavoriteStreamsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;
    private final StreamRepository localStreamRepository;
    private final WatchersRepository watchersRepository;
    private final SessionRepository sessionRepository;

    private Callback<List<StreamSearchResult>> callback;
    private boolean loadLocalOnly = false;

    @Inject public GetFavoriteStreamsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local FavoriteRepository localFavoriteRepository,
      @Remote FavoriteRepository remoteFavoriteRepository, @Local StreamRepository localStreamRepository,
      @Local WatchersRepository watchersRepository, SessionRepository sessionRepository) {
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
        } catch (ServerCommunicationException networkError) {
            /* no-op */
        }
    }

    private void loadFavoritesFrom(FavoriteRepository favoriteRepository) {
        List<Favorite> favorites = favoriteRepository.getFavorites();
        List<Stream> favoriteStreams = streamsFromFavorites(favorites);
        favoriteStreams = sortStreamsByName(favoriteStreams);
        List<StreamSearchResult> favoriteStreamsWithWatchers = addWatchersToStreams(favoriteStreams);
        markWatchingStream(favoriteStreamsWithWatchers);
        notifyLoaded(favoriteStreamsWithWatchers);
    }

    private void markWatchingStream(List<StreamSearchResult> streams) {
        String watchingId = sessionRepository.getCurrentUser().getIdWatchingStream();
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

    private List<String> idsFromFavorites(List<Favorite> favorites) {
        List<String> ids = new ArrayList<>(favorites.size());
        for (Favorite favorite : favorites) {
            ids.add(favorite.getIdStream());
        }
        return ids;
    }

    private List<Stream> streamsFromFavorites(List<Favorite> favorites) {
        List<String> idStreams = new ArrayList<>();
        for (Favorite favorite : favorites) {
            idStreams.add(favorite.getIdStream());
        }
        return localStreamRepository.getStreamsByIds(idStreams);
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
