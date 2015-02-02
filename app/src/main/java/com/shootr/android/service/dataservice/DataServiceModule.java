package com.shootr.android.service.dataservice;

import com.shootr.android.domain.utils.TimeUtils;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.EventEntityMapper;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.mappers.TeamMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.db.mappers.WatchMapper;
import com.shootr.android.service.dataservice.dto.EventDtoFactory;
import com.shootr.android.service.dataservice.dto.ShotDtoFactory;
import com.shootr.android.service.dataservice.dto.TimelineDtoFactory;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.service.dataservice.dto.UtilityDtoFactory;
import javax.inject.Singleton;

@Module(
        injects = {
                ShootrDataService.class
        },
        library = true,
        complete = false
)
public class DataServiceModule {

    @Provides @Singleton UtilityDtoFactory utilityDtoFactory() {
        return new UtilityDtoFactory();
    }

    @Provides @Singleton UserDtoFactory provideUserDtoFactory(UtilityDtoFactory utilityDtoFactory, UserMapper userMapper, FollowMapper followMapper) {
        return new UserDtoFactory(utilityDtoFactory, userMapper, followMapper);
    }

    @Provides @Singleton TimelineDtoFactory provideTimelineDtoFactory(UtilityDtoFactory utilityDtoFactory, ShotMapper shotMapper) {
        return new TimelineDtoFactory(utilityDtoFactory, shotMapper);
    }

    @Provides @Singleton ShotDtoFactory provideShotDtoFactory(UtilityDtoFactory utilityDtoFactory, ShotMapper shotMapper) {
        return new ShotDtoFactory(utilityDtoFactory, shotMapper);
    }
    @Provides @Singleton EventDtoFactory provideEventDtoFactory(UtilityDtoFactory utilityDtoFactory, EventEntityMapper eventEntityMapper, WatchMapper watchMapper, TeamMapper teamMapper,
      TimeUtils timeUtils){
        return  new EventDtoFactory(utilityDtoFactory, eventEntityMapper, watchMapper, timeUtils);
    }
}
