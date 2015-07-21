package com.shootr.android.db;

import com.shootr.android.data.mapper.DatabaseUserAvatarUrlProvider;
import com.shootr.android.data.mapper.UserAvatarUrlProvider;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.StreamEntityMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import dagger.Module;
import dagger.Provides;
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

    @Provides UserEntityModelMapper provideUserVOMapper() {
        return new UserEntityModelMapper();
    }

    @Provides @Singleton UserModelMapper provideUserModelMapper() {
        return new UserModelMapper();
    }

    @Provides UserMapper provideUserMapper() {
        return new UserMapper();
    }

    @Provides StreamEntityMapper provideEntityMapper() {
        return new StreamEntityMapper();
    }

    @Provides UserAvatarUrlProvider provideUserAvatarUrlProvider(DatabaseUserAvatarUrlProvider userAvatarUrlProvider) {
        return userAvatarUrlProvider;
    }

}
