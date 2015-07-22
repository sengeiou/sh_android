package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import javax.inject.Inject;

public class GetFavoriteStatusInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final FavoriteRepository localFavoriteRepository;

    private Callback<Boolean> callback;
    private String eventId;

    @Inject public GetFavoriteStatusInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local FavoriteRepository localFavoriteRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localFavoriteRepository = localFavoriteRepository;
    }

    public void loadFavoriteStatus(String eventId, Interactor.Callback<Boolean> callback) {
        this.callback = callback;
        this.eventId = eventId;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadFavoriteStatusFromLocal();
    }

    private void loadFavoriteStatusFromLocal() {
        Favorite favoriteStatus = localFavoriteRepository.getFavoriteByStream(eventId);
        checkFavoriteEventStatus(favoriteStatus);
    }

    private void checkFavoriteEventStatus(Favorite favoriteStatus) {
        if(favoriteStatus == null){
            notifyLoaded(false);
        }else{
            notifyLoaded(true);
        }
    }

    private void notifyLoaded(final Boolean isFavorite) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(isFavorite);
            }
        });
    }
}
