package com.shootr.android.domain.repository;

import com.shootr.android.domain.EventInfo;

public interface EventInfoRepository {

    interface EventInfoCallback extends ErrorCallback{

        void onLoaded(EventInfo eventInfo);

    }

    public void loadVisibleEventInfo(EventInfoCallback callback);
}
