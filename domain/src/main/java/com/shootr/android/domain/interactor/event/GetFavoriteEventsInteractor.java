package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class GetFavoriteEventsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;
    private final StreamRepository localStreamRepository;
    private final WatchersRepository watchersRepository;

    private Callback<List<StreamSearchResult>> callback;

    @Inject public GetFavoriteEventsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local FavoriteRepository localFavoriteRepository,
      @Remote FavoriteRepository remoteFavoriteRepository, @Local StreamRepository localStreamRepository,
      @Local WatchersRepository watchersRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFavoriteRepository = localFavoriteRepository;
        this.remoteFavoriteRepository = remoteFavoriteRepository;
        this.localStreamRepository = localStreamRepository;
        this.watchersRepository = watchersRepository;
    }

    public void loadFavoriteEvents(Interactor.Callback<List<StreamSearchResult>> callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadLocalFavorites();
        loadRemoteFavorites();
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
        List<Stream> favoriteStreams = eventsFromFavorites(favorites);
        favoriteStreams = sortEventsByName(favoriteStreams);
        List<StreamSearchResult> favoriteEventsWithWatchers = addWatchersToEvents(favoriteStreams);
        notifyLoaded(favoriteEventsWithWatchers);
    }

    private List<Stream> sortEventsByName(List<Stream> streams) {
        Collections.sort(streams, new Stream.StreamNameComparator());
        return streams;
    }

    private List<String> idsFromFavorites(List<Favorite> favorites) {
        List<String> ids = new ArrayList<>(favorites.size());
        for (Favorite favorite : favorites) {
            ids.add(favorite.getIdEvent());
        }
        return ids;
    }

    private List<Stream> eventsFromFavorites(List<Favorite> favorites) {
        List<String> idEvents = new ArrayList<>();
        for (Favorite favorite : favorites) {
            idEvents.add(favorite.getIdEvent());
        }
        return localStreamRepository.getStreamsByIds(idEvents);
    }

    private List<StreamSearchResult> addWatchersToEvents(List<Stream> streams) {
        Map<String, Integer> watchersInEvents = watchersRepository.getWatchers();
        List<StreamSearchResult> eventsWithWatchers = new ArrayList<>(streams.size());
        for (Stream stream : streams) {
            Integer eventWatchers = watchersInEvents.get(stream.getId());
            eventsWithWatchers.add(new StreamSearchResult(stream, eventWatchers));
        }
        return eventsWithWatchers;
    }

    private void notifyLoaded(final List<StreamSearchResult> streamSearchResults) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(streamSearchResults);
            }
        });
    }
}
