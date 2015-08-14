package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import javax.inject.Inject;

public class RemoveFromFavoritesInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;

    private CompletedCallback callback;

    private String idStream;

    @Inject public RemoveFromFavoritesInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local FavoriteRepository localFavoriteRepository,
      @Remote FavoriteRepository remoteFavoriteRepository) {
        this.localFavoriteRepository = localFavoriteRepository;
        this.interactorHandler = interactorHandler;
        this.remoteFavoriteRepository = remoteFavoriteRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void removeFromFavorites(String idStream, CompletedCallback callback) {
        this.callback = callback;
        this.idStream = idStream;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        Favorite existingFavorite = localFavoriteRepository.getFavoriteByStream(idStream);
        if (existingFavorite != null) {
            localFavoriteRepository.removeFavoriteByStream(existingFavorite.getIdStream());
            notifyCompleted();
            remoteFavoriteRepository.removeFavoriteByStream(existingFavorite.getIdStream());
        }
    }

    protected void notifyCompleted() {
        postExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onCompleted();
            }
        });
    }
}
