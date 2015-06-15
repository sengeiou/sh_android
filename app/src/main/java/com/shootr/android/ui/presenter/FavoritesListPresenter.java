package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetFavoriteEventsInteractor;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.views.FavoritesListView;
import java.util.List;
import javax.inject.Inject;

public class FavoritesListPresenter implements Presenter{

    private final GetFavoriteEventsInteractor getFavoriteEventsInteractor;
    private final EventModelMapper eventModelMapper;

    private FavoritesListView favoritesListView;

    @Inject public FavoritesListPresenter(GetFavoriteEventsInteractor getFavoriteEventsInteractor,
      EventModelMapper eventModelMapper) {
        this.getFavoriteEventsInteractor = getFavoriteEventsInteractor;
        this.eventModelMapper = eventModelMapper;
    }

    protected void setView(FavoritesListView favoritesListView) {
        this.favoritesListView = favoritesListView;
    }

    public void initialize(FavoritesListView favoritesListView) {
        setView(favoritesListView);
        this.loadFavorites();
    }

    private void loadFavorites() {
        favoritesListView.showLoading();
        getFavoriteEventsInteractor.loadFavoriteEvents(new Interactor.Callback<List<Event>>() {
            @Override
            public void onLoaded(List<Event> events) {
                favoritesListView.hideLoading();
                if (events.isEmpty()) {
                    favoritesListView.showEmpty();
                } else {
                    List<EventModel> eventModels = eventModelMapper.transform(events);
                    favoritesListView.showFavorites(eventModels);
                }
            }
        });
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }
}
