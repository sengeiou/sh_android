package com.shootr.android.db;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.mappers.BlockEntityDBMapper;
import com.shootr.android.db.mappers.DeviceEntityDBMapper;
import com.shootr.android.db.mappers.FollowEntityDBMapper;
import com.shootr.android.db.mappers.ShotEntityDBMapper;
import com.shootr.android.db.mappers.UserEntityDBMapper;
import com.shootr.android.domain.repository.SessionRepository;
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

    @Provides @Singleton FollowManager provideFollowManager(SQLiteOpenHelper openHelper, FollowEntityDBMapper followMapper, BlockEntityDBMapper blockEntityDBMapper) {
        return new FollowManager(openHelper, followMapper, blockEntityDBMapper);
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
