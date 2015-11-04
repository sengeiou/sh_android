package com.shootr.mobile.db;

import com.shootr.mobile.db.mappers.BlockEntityDBMapper;
import com.shootr.mobile.db.mappers.DeviceEntityDBMapper;
import com.shootr.mobile.db.mappers.FollowEntityDBMapper;
import com.shootr.mobile.db.mappers.StreamEntityDBMapper;
import com.shootr.mobile.db.mappers.SuggestedPeopleDBMapper;
import com.shootr.mobile.db.mappers.UserEntityDBMapper;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.model.mappers.UserEntityModelMapper;
import dagger.Module;
import dagger.Provides;

@Module(
  complete = false,
  library = true,
  injects = {
    StreamResultModelMapper.class
  }
)
public class MapperModule {

    @Provides
    FollowEntityDBMapper provideFollowMapper() {
        return new FollowEntityDBMapper();
    }

    @Provides
    DeviceEntityDBMapper provideDeviceMapper() {
        return new DeviceEntityDBMapper();
    }

    @Provides UserEntityModelMapper provideUserVOMapper() {
        return new UserEntityModelMapper();
    }

    @Provides
    UserEntityDBMapper provideUserMapper() {
        return new UserEntityDBMapper();
    }

    @Provides
    SuggestedPeopleDBMapper provideSuggestedPeopleMapper(UserEntityDBMapper userEntityDBMapper) {
        return new SuggestedPeopleDBMapper(userEntityDBMapper);
    }

    @Provides
    StreamEntityDBMapper provideEntityMapper() {
        return new StreamEntityDBMapper();
    }

    @Provides BlockEntityDBMapper provideBlockMapper() {
        return new BlockEntityDBMapper();
    }

}
