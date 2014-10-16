package gm.mobi.android.db;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.db.mappers.DeviceMapper;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.ui.model.ShotVO;
import gm.mobi.android.ui.model.mappers.ShotVOMapper;
import gm.mobi.android.ui.model.mappers.UserVOMapper;

@Module(
  complete = false,
  library = true)
public class MapperModule {

    @Provides ShotVOMapper provideShotVOMapper(){ return new ShotVOMapper();}

    @Provides FollowMapper provideFollowMapper() {
        return new FollowMapper();
    }

    @Provides TeamMapper provideTeamMapper() {
        return new TeamMapper();
    }

    @Provides DeviceMapper provideDeviceMapper() {
        return new DeviceMapper();
    }

    @Provides ShotMapper provideShotMapper() {
        return new ShotMapper();
    }

    @Provides UserVOMapper provideUserVOMapper(){ return new UserVOMapper();}

    @Provides UserMapper provideUserMapper() {
        return new UserMapper();
    }
}
