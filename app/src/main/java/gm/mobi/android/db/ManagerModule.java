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
public class ManagerModule {

    @Provides UserManager provideUserManager(UserMapper userMapper) {
        return new UserManager(userMapper);
    }

    @Provides FollowManager provideFollowManager(FollowMapper followMapper) {
        return new FollowManager(followMapper);
    }

    @Provides ShotManager provideShotManager(ShotMapper shotMapper, UserMapper userMapper) {
        return new ShotManager(shotMapper, userMapper);
    }

    @Provides TeamManager provideTeamManager(TeamMapper teamMapper) {
        return new TeamManager(teamMapper);
    }

    @Provides DeviceManager provideDeviceManager(DeviceMapper deviceMapper) {
        return new DeviceManager(deviceMapper);
    }
}
