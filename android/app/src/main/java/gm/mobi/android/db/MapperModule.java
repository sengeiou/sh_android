package gm.mobi.android.db;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.db.mappers.DeviceMapper;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.MatchMapper;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.mappers.WatchMapper;
import gm.mobi.android.ui.model.UserWatchingModel;
import gm.mobi.android.ui.model.mappers.MatchModelMapper;
import gm.mobi.android.ui.model.mappers.ShotModelMapper;
import gm.mobi.android.ui.model.mappers.UserModelMapper;
import gm.mobi.android.ui.model.mappers.UserWatchingModelMapper;

@Module(
  complete = false,
  library = true)
public class MapperModule {

    @Provides ShotModelMapper provideShotModelMapper(){ return new ShotModelMapper();}

    @Provides MatchModelMapper provideMatchModelMapper(){ return new MatchModelMapper();}

    @Provides UserWatchingModelMapper provideUserWatchingModelMapper(){ return new UserWatchingModelMapper();}

    @Provides FollowMapper provideFollowMapper() {
        return new FollowMapper();
    }

    @Provides DeviceMapper provideDeviceMapper() {
        return new DeviceMapper();
    }

    @Provides ShotMapper provideShotMapper() {
        return new ShotMapper();
    }

    @Provides UserModelMapper provideUserVOMapper(){ return new UserModelMapper();}

    @Provides UserMapper provideUserMapper() {
        return new UserMapper();
    }

    @Provides MatchMapper provideMatchMapper(){ return new MatchMapper();}

    @Provides WatchMapper provideWatchMapper(){ return new WatchMapper();}

    @Provides TeamMapper provideTeamMapper(){ return new TeamMapper();}

}
