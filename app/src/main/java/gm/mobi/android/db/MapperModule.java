package gm.mobi.android.db;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.db.manager.DeviceManager;
import gm.mobi.android.db.manager.FollowManager;
import gm.mobi.android.db.manager.ShotManager;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.manager.UserManager;
import gm.mobi.android.db.mappers.DeviceMapper;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.mappers.UserMapper;

@Module(
  complete = false,
  library = true)
public class MapperModule {

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

    @Provides UserMapper provideUserMapper() {
        return new UserMapper();
    }
}
