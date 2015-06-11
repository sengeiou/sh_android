package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.interactor.Interactor;
import javax.inject.Inject;

public class GetFavoriteStatusInteractor {

    @Inject public GetFavoriteStatusInteractor() {
    }

    public void loadFavoriteStatus(String eventId, Interactor.Callback<Boolean> callback) {
        //TODO real implementation
        callback.onLoaded(false);
    }

}
