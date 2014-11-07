package com.shootr.android.task.jobs.info;

import com.shootr.android.data.SessionManager;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;

public class InfoListBuilderFactory {

    public InfoListBuilder getInfoListBuilder(SessionManager sessionManager, MatchModelMapper matchModelMapper, UserWatchingModelMapper userWatchingModelMapper){
        return new InfoListBuilder(sessionManager.getCurrentUser(),matchModelMapper,userWatchingModelMapper);
    }
}
