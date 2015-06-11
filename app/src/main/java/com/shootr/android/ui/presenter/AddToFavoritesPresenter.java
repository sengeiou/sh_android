package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.AddToFavoritesView;
import javax.inject.Inject;

public class AddToFavoritesPresenter implements Presenter {

    private AddToFavoritesView addToFavoritesView;
    private String idEvent;

    @Inject
    public AddToFavoritesPresenter() {
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
        //TODO interactor
        boolean isFavorite = true;
        if (!isFavorite) {
            addToFavoritesView.showAddToFavoritesButton();
        } else {
            addToFavoritesView.hideAddToFavoritesButton();
        }
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
