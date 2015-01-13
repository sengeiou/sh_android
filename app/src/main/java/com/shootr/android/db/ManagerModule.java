package com.shootr.android.db;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.repository.EventInfoRepositoryImpl;
import com.shootr.android.data.repository.UserRepositoryImpl;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.domain.repository.EventInfoRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import dagger.Module;
import dagger.Provides;

@Module(
  complete = false,
  library = true)
public class ManagerModule {

    @Provides UserManager provideUserManager(SQLiteOpenHelper openHelper, UserMapper userMapper, SessionRepository sessionRepository) {
        return new UserManager(openHelper, userMapper, sessionRepository);
    }

    @Provides FollowManager provideFollowManager(SQLiteOpenHelper openHelper, FollowMapper followMapper) {
        return new FollowManager(openHelper, followMapper);
    }

    @Provides ShotManager provideShotManager(SQLiteOpenHelper openHelper, ShotMapper shotMapper, UserMapper userMapper, ShotModelMapper shotVOMapper) {
        return new ShotManager(openHelper, shotMapper, userMapper, shotVOMapper);
    }

    @Provides DeviceManager provideDeviceManager(SQLiteOpenHelper openHelper, DeviceMapper deviceMapper) {
        return new DeviceManager(openHelper, deviceMapper);
    }

    @Provides UserRepository provideUserRepository(UserRepositoryImpl userRepository) {
        return userRepository;
    }

    @Provides EventInfoRepository provideEventInfoRepository(EventInfoRepositoryImpl eventInfoRepository) {
        return eventInfoRepository;
    }

}
