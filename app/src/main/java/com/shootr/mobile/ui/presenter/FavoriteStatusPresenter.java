package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStatusInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.ui.views.FavoriteStatusView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class FavoriteStatusPresenter implements Presenter {

    private final GetFavoriteStatusInteractor getFavoriteStatusInteractor;
    private final AddToFavoritesInteractor addToFavoritesInteractor;
    private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    private final ErrorMessageFactory errorMessageFactory;

    private FavoriteStatusView favoriteStatusView;
    private String idStream;

    @Inject
    public FavoriteStatusPresenter(GetFavoriteStatusInteractor getFavoriteStatusInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor, RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      ErrorMessageFactory errorMessageFactory) {
        this.getFavoriteStatusInteractor = getFavoriteStatusInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(FavoriteStatusView favoriteStatusView) {
        this.favoriteStatusView = favoriteStatusView;
    }

    public void initialize(FavoriteStatusView favoriteStatusView, String idStream) {
        this.idStream = idStream;
        this.setView(favoriteStatusView);
        this.loadFavoriteStatus();
    }

    private void loadFavoriteStatus() {
        getFavoriteStatusInteractor.loadFavoriteStatus(idStream, new Interactor.Callback<Boolean>() {
            @Override public void onLoaded(Boolean isFavorite) {
                if (!isFavorite) {
                    favoriteStatusView.showAddToFavoritesButton();
                } else {
                    favoriteStatusView.showRemoveFromFavoritesButton();
                }
            }
        });
    }

    public void addToFavorites() {
        addToFavoritesInteractor.addToFavorites(idStream, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                favoriteStatusView.hideAddToFavoritesButton();
                favoriteStatusView.showRemoveFromFavoritesButton();
                favoriteStatusView.showAddedToFavorites();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showErrorInView(error);
            }
        });
    }

    private void showErrorInView(ShootrException error) {
        favoriteStatusView.showError(errorMessageFactory.getMessageForError(error));
    }

    public void removeFromFavorites() {
        removeFromFavoritesInteractor.removeFromFavorites(idStream, new Interactor.CompletedCallback() {
            @Override
            public void onCompleted() {
                favoriteStatusView.showAddToFavoritesButton();
                favoriteStatusView.hideRemoveFromFavoritesButton();
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
