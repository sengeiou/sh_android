package com.shootr.android.ui.presenter;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetFavoriteStreamsInteractor;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.FavoritesListView;
import java.util.List;
import javax.inject.Inject;

public class FavoritesListPresenter implements Presenter{

    private final GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
    private final EventResultModelMapper eventResultModelMapper;

    private FavoritesListView favoritesListView;
    private boolean hasBeenPaused = false;

    @Inject public FavoritesListPresenter(GetFavoriteStreamsInteractor getFavoriteStreamsInteractor,
      EventResultModelMapper eventResultModelMapper) {
        this.getFavoriteStreamsInteractor = getFavoriteStreamsInteractor;
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
        getFavoriteStreamsInteractor.loadFavoriteStreams(new Interactor.Callback<List<StreamSearchResult>>() {
            @Override public void onLoaded(List<StreamSearchResult> events) {
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
