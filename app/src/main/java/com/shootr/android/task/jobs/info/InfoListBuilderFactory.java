package com.shootr.android.task.jobs.info;

import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.UserEntityWatchingModelMapper;
import com.shootr.android.ui.model.mappers.MatchModelMapper;

public class InfoListBuilderFactory {

    public InfoListBuilder getInfoListBuilder(SessionRepository sessionRepository, MatchModelMapper matchModelMapper,
      UserEntityWatchingModelMapper userWatchingModelMapper, UserEntityMapper userEntityMapper){
        return new InfoListBuilder(sessionRepository.getCurrentUser(),matchModelMapper,userWatchingModelMapper,
          userEntityMapper);
    }
}
