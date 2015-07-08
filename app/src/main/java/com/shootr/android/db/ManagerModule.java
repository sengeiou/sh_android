package com.shootr.android.db;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.domain.repository.SessionRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  complete = false,
  library = true)
public class ManagerModule {

    @Provides @Singleton UserManager provideUserManager(SQLiteOpenHelper openHelper, UserMapper userMapper, SessionRepository sessionRepository) {
        return new UserManager(openHelper, userMapper, sessionRepository);
    }

    @Provides @Singleton FollowManager provideFollowManager(SQLiteOpenHelper openHelper, FollowMapper followMapper) {
        return new FollowManager(openHelper, followMapper);
    }

    @Provides @Singleton ShotManager provideShotManager(SQLiteOpenHelper openHelper,
      ShotEntityMapper shotEntityMapper,
      UserMapper userMapper) {
        return new ShotManager(openHelper, shotEntityMapper);
    }

    @Provides @Singleton DeviceManager provideDeviceManager(SQLiteOpenHelper openHelper, DeviceMapper deviceMapper) {
        return new DeviceManager(openHelper, deviceMapper);
    }
}
