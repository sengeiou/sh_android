package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class GetFavoriteEventsInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;
    private final EventRepository localEventRepository;
    private final WatchersRepository watchersRepository;

    private Callback<List<EventSearchResult>> callback;

    @Inject public GetFavoriteEventsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local FavoriteRepository localFavoriteRepository,
      @Remote FavoriteRepository remoteFavoriteRepository, @Local EventRepository localEventRepository,
      @Local WatchersRepository watchersRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFavoriteRepository = localFavoriteRepository;
        this.remoteFavoriteRepository = remoteFavoriteRepository;
        this.localEventRepository = localEventRepository;
        this.watchersRepository = watchersRepository;
    }

    public void loadFavoriteEvents(Interactor.Callback<List<EventSearchResult>> callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        loadLocalFavorites();
        loadRemoteFavorites();
    }

    private void loadLocalFavorites() {
        loadFavoritesFrom(localFavoriteRepository);
    }

    private void loadRemoteFavorites() {
        loadFavoritesFrom(remoteFavoriteRepository);
    }

    private void loadFavoritesFrom(FavoriteRepository favoriteRepository) {
        // Cargar favoritos
        List<Favorite> favorites = favoriteRepository.getFavorites();
        // Ordenar favoritos
        //TODO
        // Cargar eventos de los favoritos
        List<Event> favoriteEvents = eventsFromFavorites(favorites);
        // Adjuntar watchers
        List<EventSearchResult> favoriteEventsWithWatchers = addWatchersToEvents(favoriteEvents);
        // Devolver eventos
        notifyLoaded(favoriteEventsWithWatchers);
    }

    private List<Event> eventsFromFavorites(List<Favorite> favorites) {
        List<String> idEvents = new ArrayList<>();
        for (Favorite favorite : favorites) {
            idEvents.add(favorite.getIdEvent());
        }
        return localEventRepository.getEventsByIds(idEvents);

    }

    private List<EventSearchResult> addWatchersToEvents(List<Event> events) {
        Map<String, Integer> watchersInEvents = watchersRepository.getWatchers();
        List<EventSearchResult> eventsWithWatchers = new ArrayList<>(events.size());
        for (Event event : events) {
            Integer eventWatchers = watchersInEvents.get(event.getId());
            eventsWithWatchers.add(new EventSearchResult(event, eventWatchers));
        }
        return eventsWithWatchers;
    }

    private void notifyLoaded(final List<EventSearchResult> eventSearchResults) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(eventSearchResults);
            }
        });
    }
}
