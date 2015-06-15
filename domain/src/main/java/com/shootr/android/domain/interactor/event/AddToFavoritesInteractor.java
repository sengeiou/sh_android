package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import javax.inject.Inject;

public class AddToFavoritesInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final FavoriteRepository localFavoriteRepository;

    private Interactor.CompletedCallback callback;

    private String idEvent;

    @Inject public AddToFavoritesInteractor(FavoriteRepository localFavoriteRepository,
      InteractorHandler interactorHandler, SessionRepository sessionRepository) {
        this.localFavoriteRepository = localFavoriteRepository;
        this.interactorHandler = interactorHandler;
        this.sessionRepository = sessionRepository;
    }

    public void addToFavorites(String idEvent, Interactor.CompletedCallback callback) {
        this.callback = callback;
        this.idEvent = idEvent;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Favorite favorite = favoriteFromParameters();
        localFavoriteRepository.putFavorite(favorite);
        callback.onCompleted();
    }

    private Favorite favoriteFromParameters() {
        Favorite favorite = new Favorite();
        favorite.setIdEvent(idEvent);
        return favorite;
    }
}
