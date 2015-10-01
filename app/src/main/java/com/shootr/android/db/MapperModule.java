package com.shootr.android.db;

import com.shootr.android.db.mappers.DeviceEntityDBMapper;
import com.shootr.android.db.mappers.FollowEntityDBMapper;
import com.shootr.android.db.mappers.StreamEntityDBMapper;
import com.shootr.android.db.mappers.SuggestedPeopleDBMapper;
import com.shootr.android.db.mappers.UserEntityDBMapper;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.model.mappers.UserEntityModelMapper;
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
    SuggestedPeopleDBMapper provideSuggestedPeopleMapper() {
        return new SuggestedPeopleDBMapper();
    }

    @Provides
    StreamEntityDBMapper provideEntityMapper() {
        return new StreamEntityDBMapper();
    }

}
