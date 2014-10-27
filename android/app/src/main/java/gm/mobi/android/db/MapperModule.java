package gm.mobi.android.db;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.db.mappers.DeviceMapper;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.ui.model.mappers.ShotModelMapper;
import gm.mobi.android.ui.model.mappers.UserModelMapper;

@Module(
  complete = false,
  library = true)
public class MapperModule {

    @Provides ShotModelMapper provideShotVOMapper(){ return new ShotModelMapper();}

    @Provides FollowMapper provideFollowMapper() {
        return new FollowMapper();
    }

    @Provides DeviceMapper provideDeviceMapper() {
        return new DeviceMapper();
    }

    @Provides ShotMapper provideShotMapper() {
        return new ShotMapper();
    }

    @Provides UserModelMapper provideUserVOMapper(){ return new UserModelMapper();}

    @Provides UserMapper provideUserMapper() {
        return new UserMapper();
    }
}
