package com.shootr.android.db;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.repository.EventInfoRepositoryImpl;
import com.shootr.android.data.repository.EventSearchRepositoryImpl;
import com.shootr.android.data.repository.UserRepositoryImpl;
import com.shootr.android.data.repository.WatchRepositoryImpl;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.manager.UserManager;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.domain.repository.EventInfoRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
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

    @Provides @Singleton ShotManager provideShotManager(SQLiteOpenHelper openHelper, ShotMapper shotMapper, UserMapper userMapper, ShotModelMapper shotVOMapper) {
        return new ShotManager(openHelper, shotMapper, userMapper, shotVOMapper);
    }

    @Provides @Singleton DeviceManager provideDeviceManager(SQLiteOpenHelper openHelper, DeviceMapper deviceMapper) {
        return new DeviceManager(openHelper, deviceMapper);
    }

    @Provides @Singleton UserRepository provideUserRepository(UserRepositoryImpl userRepository) {
        return userRepository;
    }

    @Provides @Singleton EventInfoRepository provideEventInfoRepository(EventInfoRepositoryImpl eventInfoRepository) {
        return eventInfoRepository;
    }

    @Provides @Singleton WatchRepository provideWatchRepository(WatchRepositoryImpl watchRepository) {
        return watchRepository;
    }

    @Provides @Singleton EventSearchRepository provideWatchRepository(EventSearchRepositoryImpl eventSearchRepository) {
        return eventSearchRepository;
    }
}
