package com.shootr.android.task.jobs.info;

import com.shootr.android.data.mapper.UserEntityMapper;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.EventEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityWatchingModelMapper;

public class InfoListBuilderFactory {

    public InfoListBuilder getInfoListBuilder(SessionRepository sessionRepository, EventEntityModelMapper eventEntityModelMapper,
      UserEntityWatchingModelMapper userWatchingModelMapper, UserEntityMapper userEntityMapper){
        return new InfoListBuilder(sessionRepository.getCurrentUser(), eventEntityModelMapper,userWatchingModelMapper,
          userEntityMapper);
    }
}
