package com.shootr.android.db;

import dagger.Module;
import dagger.Provides;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.ui.model.mappers.ShotModelMapper;

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

    @Provides DeviceManager provideDeviceManager(DeviceMapper deviceMapper) {
        return new DeviceManager(deviceMapper);
    }
}
