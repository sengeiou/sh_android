package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetFavoriteEventsInteractor;
import com.shootr.android.ui.views.FavoritesListView;
import java.util.List;
import javax.inject.Inject;

public class FavoritesListPresenter implements Presenter{

    private final GetFavoriteEventsInteractor getFavoriteEventsInteractor;

    private FavoritesListView favoritesListView;

    @Inject public FavoritesListPresenter(GetFavoriteEventsInteractor getFavoriteEventsInteractor) {
        this.getFavoriteEventsInteractor = getFavoriteEventsInteractor;
    }

    protected void setView(FavoritesListView favoritesListView) {
        this.favoritesListView = favoritesListView;
    }

    public void initialize(FavoritesListView favoritesListView) {
        setView(favoritesListView);
        this.loadFavorites();
    }

    private void loadFavorites() {
        getFavoriteEventsInteractor.loadFavoriteEvents(new Interactor.Callback<List<Event>>() {
            @Override
            public void onLoaded(List<Event> events) {
                //TODO
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
