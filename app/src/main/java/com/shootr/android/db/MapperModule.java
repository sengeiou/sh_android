package com.shootr.android.db;

import android.app.Application;
import com.shootr.android.ui.model.mappers.UserEntityWatchingModelMapper;
import com.shootr.android.ui.model.mappers.TeamModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.model.mappers.WatchingRequestModelMapper;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.EventEntityMapper;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.mappers.TeamMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.db.mappers.WatchMapper;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import javax.inject.Singleton;

@Module(
  complete = false,
  library = true,
  injects = {
    TeamModelMapper.class,
    TeamMapper.class,
    WatchingRequestModelMapper.class,
  }
)
public class MapperModule {

    @Provides ShotModelMapper provideShotModelMapper() {
        return new ShotModelMapper();
    }

    @Provides UserEntityWatchingModelMapper provideUserWatchingModelMapper(Application application) {
        return new UserEntityWatchingModelMapper(application);
    }

    @Provides FollowMapper provideFollowMapper() {
        return new FollowMapper();
    }

    @Provides DeviceMapper provideDeviceMapper() {
        return new DeviceMapper();
    }

    @Provides ShotMapper provideShotMapper() {
        return new ShotMapper();
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

    @Provides WatchMapper provideWatchMapper() {
        return new WatchMapper();
    }

    @Provides TeamMapper provideTeamMapper() {
        return new TeamMapper();
    }

}
