package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetFavoriteEventsInteractor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.FavoritesListView;
import java.util.List;
import javax.inject.Inject;

public class FavoritesListPresenter implements Presenter{

    private final GetFavoriteEventsInteractor getFavoriteEventsInteractor;
    private final SelectEventInteractor selectEventInteractor;
    private final EventResultModelMapper eventResultModelMapper;
    private final EventModelMapper eventModelMapper;

    private FavoritesListView favoritesListView;
    private boolean hasBeenPaused = false;

    @Inject public FavoritesListPresenter(GetFavoriteEventsInteractor getFavoriteEventsInteractor,
      SelectEventInteractor selectEventInteractor,
      EventResultModelMapper eventResultModelMapper, EventModelMapper eventModelMapper) {
        this.getFavoriteEventsInteractor = getFavoriteEventsInteractor;
        this.selectEventInteractor = selectEventInteractor;
        this.eventResultModelMapper = eventResultModelMapper;
        this.eventModelMapper = eventModelMapper;
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
        selectEventInteractor.selectEvent(idEvent, new Interactor.Callback<EventSearchResult>() {
            @Override public void onLoaded(EventSearchResult selectedEvent) {
                onEventSelected(eventModelMapper.transform(selectedEvent.getEvent()));
            }
        });
    }

    private void onEventSelected(EventModel selectedEvent) {
        favoritesListView.navigateToEventTimeline(selectedEvent.getIdEvent(), selectedEvent.getTitle());
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
