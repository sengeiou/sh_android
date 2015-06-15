package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class AddToFavoritesInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;

    private Interactor.CompletedCallback callback;

    private String idEvent;

    @Inject public AddToFavoritesInteractor(@Local FavoriteRepository localFavoriteRepository,
      @Remote FavoriteRepository remoteFavoriteRepository, InteractorHandler interactorHandler) {
        this.localFavoriteRepository = localFavoriteRepository;
        this.interactorHandler = interactorHandler;
        this.remoteFavoriteRepository = remoteFavoriteRepository;
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
        remoteFavoriteRepository.putFavorite(favorite);
    }

    private Favorite favoriteFromParameters() {
        Favorite favorite = new Favorite();
        favorite.setIdEvent(idEvent);
        favorite.setOrder(getNextOrder());
        return favorite;
    }

    private int getNextOrder() {
        Favorite lastLocalFavorite = getLastLocalFavorite();
        if(lastLocalFavorite != null){
            return lastLocalFavorite.getOrder() + 1;
        }else{
            return 0;
        }
    }

    private Favorite getLastLocalFavorite() {
        List<Favorite> favorites = localFavoriteRepository.getFavorites();
        Collections.sort(favorites, new Favorite.AscendingOrderComparator());
        if(!favorites.isEmpty()){
            return favorites.get(favorites.size() - 1);
        }else{
            return null;
        }
    }
}
