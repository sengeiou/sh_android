package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.interactor.Interactor;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetFavoriteEventsInteractor  {

    @Inject public GetFavoriteEventsInteractor() {
    }

    public void loadFavoriteEvents(Interactor.Callback<List<Event>> callback) {
        callback.onLoaded(Collections.EMPTY_LIST);
    }

}
