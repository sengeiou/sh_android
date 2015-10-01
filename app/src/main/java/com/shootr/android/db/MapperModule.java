package com.shootr.android.db;

import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.StreamEntityMapper;
import com.shootr.android.db.mappers.SuggestedPeopleMapper;
import com.shootr.android.db.mappers.UserMapper;
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

    @Provides FollowMapper provideFollowMapper() {
        return new FollowMapper();
    }

    @Provides DeviceMapper provideDeviceMapper() {
        return new DeviceMapper();
    }

    @Provides UserEntityModelMapper provideUserVOMapper() {
        return new UserEntityModelMapper();
    }

    @Provides UserMapper provideUserMapper() {
        return new UserMapper();
    }

    @Provides SuggestedPeopleMapper provideSuggestedPeopleMapper() {
        return new SuggestedPeopleMapper();
    }

    @Provides StreamEntityMapper provideEntityMapper() {
        return new StreamEntityMapper();
    }

}
