package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStatusInteractor;
import com.shootr.mobile.domain.interactor.stream.GetMutedStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.ui.views.FavoriteStatusView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.List;
import javax.inject.Inject;

public class FavoriteStatusPresenter implements Presenter {

    private final GetFavoriteStatusInteractor getFavoriteStatusInteractor;
    private final AddToFavoritesInteractor addToFavoritesInteractor;
    private final RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    private final GetMutedStreamsInteractor getMutedStreamsInteractor;
    private final MuteInteractor muteInteractor;
    private final UnmuteInteractor unmuteInteractor;

    private final ErrorMessageFactory errorMessageFactory;

    private FavoriteStatusView favoriteStatusView;
    private String idStream;
    private boolean hasBeenPaused;

    @Inject
    public FavoriteStatusPresenter(GetFavoriteStatusInteractor getFavoriteStatusInteractor,
      AddToFavoritesInteractor addToFavoritesInteractor, RemoveFromFavoritesInteractor removeFromFavoritesInteractor,
      GetMutedStreamsInteractor getMutedStreamsInteractor, MuteInteractor muteInteractor,
      UnmuteInteractor unmuteInteractor, ErrorMessageFactory errorMessageFactory) {
        this.getFavoriteStatusInteractor = getFavoriteStatusInteractor;
        this.addToFavoritesInteractor = addToFavoritesInteractor;
        this.removeFromFavoritesInteractor = removeFromFavoritesInteractor;
        this.getMutedStreamsInteractor = getMutedStreamsInteractor;
        this.muteInteractor = muteInteractor;
        this.unmuteInteractor = unmuteInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    public void setView(FavoriteStatusView favoriteStatusView) {
        this.favoriteStatusView = favoriteStatusView;
    }

    public void initialize(FavoriteStatusView favoriteStatusView, String idStream) {
        this.idStream = idStream;
        this.setView(favoriteStatusView);
        this.loadFavoriteStatus();
        this.loadMuteStatus();
    }

    private void loadMuteStatus() {
        getMutedStreamsInteractor.loadMutedStreamIds(new Interactor.Callback<List<String>>() {
            @Override public void onLoaded(List<String> ids) {
                if (ids.contains(idStream)) {
                    favoriteStatusView.showUnmuteButton();
                } else {
                    favoriteStatusView.showMuteButton();
                }
            }
        });
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
            @Override public void onCompleted() {
                favoriteStatusView.showAddToFavoritesButton();
                favoriteStatusView.hideRemoveFromFavoritesButton();
            }
        });
    }

    public void mute() {
        muteInteractor.mute(idStream, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                favoriteStatusView.hideMuteButton();
            }
        });
    }

    public void unmute() {
        unmuteInteractor.unmute(idStream, new Interactor.CompletedCallback() {
            @Override public void onCompleted() {
                favoriteStatusView.hideUnmuteButton();
            }
        });
    }

    @Override
    public void resume() {
        if (hasBeenPaused) {
            loadMuteStatus();
        }
    }

    @Override
    public void pause() {
        hasBeenPaused = true;
    }
}
