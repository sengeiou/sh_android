package com.shootr.mobile.db;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.db.manager.DeviceManager;
import com.shootr.mobile.db.manager.FollowManager;
import com.shootr.mobile.db.manager.ShotManager;
import com.shootr.mobile.db.manager.UserManager;
import com.shootr.mobile.db.mappers.BanEntityDBMapper;
import com.shootr.mobile.db.mappers.BlockEntityDBMapper;
import com.shootr.mobile.db.mappers.DeviceEntityDBMapper;
import com.shootr.mobile.db.mappers.FollowEntityDBMapper;
import com.shootr.mobile.db.mappers.ShotEntityDBMapper;
import com.shootr.mobile.db.mappers.UserEntityDBMapper;
import com.shootr.mobile.domain.repository.SessionRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  complete = false,
  library = true)
public class ManagerModule {

    @Provides @Singleton UserManager provideUserManager(SQLiteOpenHelper openHelper, UserEntityDBMapper userMapper, SessionRepository sessionRepository) {
        return new UserManager(openHelper, userMapper);
    }

    @Provides @Singleton FollowManager provideFollowManager(SQLiteOpenHelper openHelper, FollowEntityDBMapper followMapper, BlockEntityDBMapper blockEntityDBMapper, BanEntityDBMapper banEntityDBMapper) {
        return new FollowManager(openHelper, followMapper, blockEntityDBMapper, banEntityDBMapper);
    }

    @Provides @Singleton ShotManager provideShotManager(SQLiteOpenHelper openHelper,
      ShotEntityDBMapper shotEntityMapper,
      UserEntityDBMapper userMapper) {
        return new ShotManager(openHelper, shotEntityMapper);
    }

    @Provides @Singleton DeviceManager provideDeviceManager(SQLiteOpenHelper openHelper, DeviceEntityDBMapper deviceMapper) {
        return new DeviceManager(openHelper, deviceMapper);
    }
}
