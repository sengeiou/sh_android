package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.interactor.Interactor;
import javax.inject.Inject;

public class AddToFavoritesInteractor {

    @Inject public AddToFavoritesInteractor() {
    }

    public void addToFavorites(String idEvent, Interactor.CompletedCallback callback) {
        callback.onCompleted();
    }

}
