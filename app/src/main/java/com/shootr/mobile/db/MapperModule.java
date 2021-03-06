package com.shootr.mobile.db;

import com.shootr.mobile.db.mappers.BlockEntityDBMapper;
import com.shootr.mobile.db.mappers.ContributorDBMapper;
import com.shootr.mobile.db.mappers.DeviceEntityDBMapper;
import com.shootr.mobile.db.mappers.FollowEntityDBMapper;
import com.shootr.mobile.db.mappers.StreamEntityDBMapper;
import com.shootr.mobile.db.mappers.UserEntityDBMapper;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import dagger.Module;
import dagger.Provides;

@Module(
    complete = false,
    library = true,
    injects = {
        StreamResultModelMapper.class
    }) public class MapperModule {

  @Provides FollowEntityDBMapper provideFollowMapper() {
    return new FollowEntityDBMapper();
  }

  @Provides ContributorDBMapper provideContributorDBMapper() {
    return new ContributorDBMapper();
  }

  @Provides DeviceEntityDBMapper provideDeviceMapper() {
    return new DeviceEntityDBMapper();
  }

  @Provides UserEntityDBMapper provideUserMapper() {
    return new UserEntityDBMapper();
  }

  @Provides StreamEntityDBMapper provideEntityMapper(SessionRepository sessionRepository) {
    return new StreamEntityDBMapper(sessionRepository);
  }

  @Provides BlockEntityDBMapper provideBlockMapper() {
    return new BlockEntityDBMapper();
  }

}
