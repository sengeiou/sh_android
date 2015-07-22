package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class RemoveFromFavoritesInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FavoriteRepository localFavoriteRepository;
    private final FavoriteRepository remoteFavoriteRepository;

    private CompletedCallback callback;

    private String idEvent;

    @Inject public RemoveFromFavoritesInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local FavoriteRepository localFavoriteRepository,
      @Remote FavoriteRepository remoteFavoriteRepository) {
        this.localFavoriteRepository = localFavoriteRepository;
        this.interactorHandler = interactorHandler;
        this.remoteFavoriteRepository = remoteFavoriteRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void removeFromFavorites(String idEvent, CompletedCallback callback) {
        this.callback = callback;
        this.idEvent = idEvent;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        Favorite existingFavorite = localFavoriteRepository.getFavoriteByEvent(idEvent);
        checkNotNull(existingFavorite);
        localFavoriteRepository.removeFavoriteByEvent(existingFavorite.getIdStream());
        notifyCompleted();
        remoteFavoriteRepository.removeFavoriteByEvent(existingFavorite.getIdStream());
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
