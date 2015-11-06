package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Favorite;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.FavoriteAdded;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.FavoriteRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.service.StreamIsAlreadyInFavoritesException;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class AddToFavoritesInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;
    private final SessionRepository sessionRepository;
    private final BusPublisher busPublisher;

    private Interactor.CompletedCallback callback;
    private ErrorCallback errorCallback;

    private String idStream;

    @Inject public AddToFavoritesInteractor(@Local FavoriteRepository localFavoriteRepository,
      @Remote FavoriteRepository remoteFavoriteRepository, InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, SessionRepository sessionRepository, BusPublisher busPublisher) {
        this.localFavoriteRepository = localFavoriteRepository;
        this.interactorHandler = interactorHandler;
        this.remoteFavoriteRepository = remoteFavoriteRepository;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
        this.busPublisher = busPublisher;
    }

    public void addToFavorites(String idStream, CompletedCallback callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.idStream = idStream;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        Favorite favorite = favoriteFromParameters();
        localFavoriteRepository.putFavorite(favorite);
        notifyLoaded();
        notifyAdditionToBus();
        try {
            remoteFavoriteRepository.putFavorite(favorite);
        } catch (StreamAlreadyInFavoritesException error) {
            notifyError(new StreamIsAlreadyInFavoritesException(error));
        }
    }

    private Favorite favoriteFromParameters() {
        Favorite favorite = new Favorite();
        favorite.setIdStream(idStream);
        favorite.setOrder(getNextOrder());
        return favorite;
    }

    private int getNextOrder() {
        Favorite lastLocalFavorite = getLastLocalFavorite();
        if (lastLocalFavorite != null) {
            return lastLocalFavorite.getOrder() + 1;
        } else {
            return 0;
        }
    }

    private Favorite getLastLocalFavorite() {
        List<Favorite> favorites = localFavoriteRepository.getFavorites(sessionRepository.getCurrentUserId());
        Collections.sort(favorites, new Favorite.AscendingOrderComparator());
        if (!favorites.isEmpty()) {
            return favorites.get(favorites.size() - 1);
        } else {
            return null;
        }
    }

    protected void notifyLoaded() {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onCompleted();
            }
        });
    }

    private void notifyError(final ShootrException e) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(e);
            }
        });
    }

    protected void notifyAdditionToBus() {
        busPublisher.post(new FavoriteAdded.Event());
    }
}
