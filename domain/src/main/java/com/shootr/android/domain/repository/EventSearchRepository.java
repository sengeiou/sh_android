package com.shootr.android.domain.repository;

import com.shootr.android.domain.EventSearchResult;
import java.util.List;

public interface EventSearchRepository {

    void getDefaultEvents(EventResultListCallback callback);

    interface EventResultListCallback extends ErrorCallback {

        void onLoaded(List<EventSearchResult> events);
    }
}
