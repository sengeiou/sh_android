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
        loadFavoriteEventsFromLocalRepository();
        loadFavoriteEventsFromRemoteRepository();
    }

    //region Load Favorites
    private void loadFavoriteEventsFromLocalRepository() {
        List<String> idEvents = getFavoriteEventIdsFromRepository(localFavoriteRepository);
        obtainEventSearchResultsFromIdEvents(idEvents);
    }

    private void loadFavoriteEventsFromRemoteRepository() {
        List<String> idEvents = getFavoriteEventIdsFromRepository(remoteFavoriteRepository);
        obtainEventSearchResultsFromIdEvents(idEvents);
    }

    public List<String> getFavoriteEventIdsFromRepository(FavoriteRepository favoriteRepository) {
        List<Favorite> favorites = favoriteRepository.getFavorites();
        List<String> idEvents = new ArrayList<>();
        for (Favorite favorite : favorites) {
            idEvents.add(favorite.getIdEvent());
        }
        return idEvents;
    }

    //endregion

    //region Obtain Event Search Results
    private void obtainEventSearchResultsFromIdEvents(List<String> idEvents) {
        List<Event> eventsByIds = localEventRepository.getEventsByIds(idEvents);
        List<EventSearchResult> eventSearchResults = obtainEventSearchResultsFromEvents(eventsByIds);
        notifyLoaded(eventSearchResults);
    }

    private List<EventSearchResult> obtainEventSearchResultsFromEvents(List<Event> eventsByIds) {
        List<EventSearchResult> eventSearchResults = new ArrayList<>();
        for (Event eventsById : eventsByIds) {
            Integer watchers = watchersRepository.getWatchers(eventsById.getId());
            EventSearchResult eventSearchResult = new EventSearchResult();
            eventSearchResult.setEvent(eventsById);
            eventSearchResult.setWatchersNumber(watchers);
            eventSearchResults.add(eventSearchResult);
        }
        return eventSearchResults;
    }
    //endregion

    private void notifyLoaded(final List<EventSearchResult> eventSearchResults) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(eventSearchResults);
            }
        });
    }
}
