package gm.mobi.android.db;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.data.SessionManager;
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
import gm.mobi.android.ui.model.mappers.ShotModelMapper;

@Module(
  complete = false,
  library = true)
public class ManagerModule {

    @Provides UserManager provideUserManager(UserMapper userMapper, SessionManager sessionManager) {
        return new UserManager(userMapper, sessionManager);
    }

    @Provides FollowManager provideFollowManager(FollowMapper followMapper) {
        return new FollowManager(followMapper);
    }

    @Provides ShotManager provideShotManager(ShotMapper shotMapper, UserMapper userMapper, ShotModelMapper shotVOMapper) {
        return new ShotManager(shotMapper, userMapper, shotVOMapper);
    }

    @Provides TeamManager provideTeamManager(TeamMapper teamMapper) {
        return new TeamManager(teamMapper);
    }

    @Provides DeviceManager provideDeviceManager(DeviceMapper deviceMapper) {
        return new DeviceManager(deviceMapper);
    }
}
