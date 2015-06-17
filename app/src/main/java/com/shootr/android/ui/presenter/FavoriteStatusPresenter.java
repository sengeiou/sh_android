package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.event.GetFavoriteStatusInteractor;
import com.shootr.android.ui.views.FavoriteStatusView;
import javax.inject.Inject;

public class FavoriteStatusPresenter implements Presenter {

    private final GetFavoriteStatusInteractor getFavoriteStatusInteractor;
    private final AddToFavoritesInteractor addToFavoritesInteractor;

    private FavoriteStatusView favoriteStatusView;
    private String idEvent;

    @Inject
    public FavoriteStatusPresenter(GetFavoriteStatusInteractor getFavoriteStatusInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor) {
        this.getFavoriteStatusInteractor = getFavoriteStatusInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
    }

    public void setView(FavoriteStatusView favoriteStatusView) {
        this.favoriteStatusView = favoriteStatusView;
    }

    public void initialize(FavoriteStatusView favoriteStatusView, String idEvent) {
        this.idEvent = idEvent;
        this.setView(favoriteStatusView);
        this.loadFavoriteStatus();
    }

    private void loadFavoriteStatus() {
        getFavoriteStatusInteractor.loadFavoriteStatus(idEvent, new Interactor.Callback<Boolean>() {
            @Override
            public void onLoaded(Boolean isFavorite) {
                if (!isFavorite) {
                    favoriteStatusView.showAddToFavoritesButton();
                } else {
                    favoriteStatusView.hideAddToFavoritesButton();
                }
            }
        });
    }

    public void addToFavorites() {
        addToFavoritesInteractor.addToFavorites(idEvent, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                favoriteStatusView.hideAddToFavoritesButton();
                favoriteStatusView.showAddedToFavorites();
            }
        });
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
