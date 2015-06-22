package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetFavoriteEventsInteractor;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.FavoritesListView;
import java.util.List;
import javax.inject.Inject;

public class FavoritesListPresenter implements Presenter{

    private final GetFavoriteEventsInteractor getFavoriteEventsInteractor;
    private final EventResultModelMapper eventResultModelMapper;

    private FavoritesListView favoritesListView;
    private boolean hasBeenPaused = false;

    @Inject public FavoritesListPresenter(GetFavoriteEventsInteractor getFavoriteEventsInteractor,
      EventResultModelMapper eventResultModelMapper) {
        this.getFavoriteEventsInteractor = getFavoriteEventsInteractor;
        this.eventResultModelMapper = eventResultModelMapper;
    }

    public void setView(FavoritesListView favoritesListView) {
        this.favoritesListView = favoritesListView;
    }

    public void initialize(FavoritesListView favoritesListView) {
        setView(favoritesListView);
        this.loadFavorites();
    }

    protected void loadFavorites() {
        favoritesListView.showLoading();
        getFavoriteEventsInteractor.loadFavoriteEvents(new Interactor.Callback<List<EventSearchResult>>() {
            @Override
            public void onLoaded(List<EventSearchResult> events) {
                favoritesListView.hideLoading();
                if (events.isEmpty()) {
                    favoritesListView.showEmpty();
                    favoritesListView.hideContent();
                } else {
                    List<EventResultModel> eventModels = eventResultModelMapper.transform(events);
                    favoritesListView.renderFavorites(eventModels);
                    favoritesListView.showContent();
                    favoritesListView.hideEmpty();
                }
            }
        });
    }

    public void selectEvent(EventResultModel event) {
        selectEvent(event.getEventModel().getIdEvent(), event.getEventModel().getTitle());
    }

    private void selectEvent(final String idEvent, String eventTitle) {
        favoritesListView.navigateToEventTimeline(idEvent, eventTitle);
    }

    @Override
    public void resume() {
        if (hasBeenPaused) {
            this.loadFavorites();
        }
    }

    @Override
    public void pause() {
        hasBeenPaused = true;
    }
}
