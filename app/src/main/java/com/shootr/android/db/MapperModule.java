package com.shootr.android.db;

import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.model.mappers.WatchingRequestModelMapper;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.EventEntityMapper;
import com.shootr.android.db.mappers.UserMapper;
import javax.inject.Singleton;

@Module(
  complete = false,
  library = true,
  injects = {
    EventResultModelMapper.class
  }
)
public class MapperModule {

    @Provides FollowMapper provideFollowMapper() {
        return new FollowMapper();
    }

    @Provides DeviceMapper provideDeviceMapper() {
        return new DeviceMapper();
    }

    @Provides ShotEntityMapper provideShotMapper() {
        return new ShotEntityMapper();
    }

    @Provides UserEntityModelMapper provideUserVOMapper() {
        return new UserEntityModelMapper();
    }

    @Provides @Singleton UserModelMapper provideUserModelMapper() {
        return new UserModelMapper();
    }

    @Provides UserMapper provideUserMapper() {
        return new UserMapper();
    }

    @Provides EventEntityMapper provideEntityMapper() {
        return new EventEntityMapper();
    }

}
