package gm.mobi.android.service.dataservice;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.MatchMapper;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.mappers.WatchMapper;
import gm.mobi.android.service.dataservice.dto.MatchDtoFactory;
import gm.mobi.android.service.dataservice.dto.ShotDtoFactory;
import gm.mobi.android.service.dataservice.dto.TimelineDtoFactory;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.service.dataservice.dto.UtilityDtoFactory;
import javax.inject.Singleton;

@Module(
        injects = {
                BagdadDataService.class
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
    @Provides @Singleton MatchDtoFactory provideMatchDtoFactory(UtilityDtoFactory utilityDtoFactory, MatchMapper matchMapper, WatchMapper watchMapper, TeamMapper teamMapper){
        return  new MatchDtoFactory(utilityDtoFactory,matchMapper, watchMapper, teamMapper);
    }
}
