package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.GetFavoriteStatusInteractor;
import com.shootr.android.ui.views.AddToFavoritesView;
import javax.inject.Inject;

public class AddToFavoritesPresenter implements Presenter {

    private final GetFavoriteStatusInteractor getFavoriteStatusInteractor;

    private AddToFavoritesView addToFavoritesView;
    private String idEvent;

    @Inject
    public AddToFavoritesPresenter(GetFavoriteStatusInteractor getFavoriteStatusInteractor) {
        this.getFavoriteStatusInteractor = getFavoriteStatusInteractor;
    }

    public void setView(AddToFavoritesView addToFavoritesView) {
        this.addToFavoritesView = addToFavoritesView;
    }

    public void initialize(AddToFavoritesView addToFavoritesView, String idEvent) {
        this.idEvent = idEvent;
        this.setView(addToFavoritesView);
        this.loadFavoriteStatus();
    }

    private void loadFavoriteStatus() {
        getFavoriteStatusInteractor.loadFavoriteStatus(idEvent, new Interactor.Callback<Boolean>() {
            @Override
            public void onLoaded(Boolean isFavorite) {
                if (!isFavorite) {
                    addToFavoritesView.showAddToFavoritesButton();
                } else {
                    addToFavoritesView.hideAddToFavoritesButton();
                }
            }
        });
    }

    public void addToFavorites() {
        //TODO interactor
        addToFavoritesView.hideAddToFavoritesButton();
        addToFavoritesView.showAddedToFavorites();
    }

    @Override
    public void resume() {
        /* no-op */
    }

    @Override
    public void pause() {
        /* no-op */
    }
}
