package com.shootr.android.db;

import android.app.Application;
import com.shootr.android.ui.model.mappers.WatchingRequestModelMapper;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.MatchMapper;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.mappers.TeamMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.db.mappers.WatchMapper;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;

@Module(
  complete = false,
  library = true)
public class MapperModule {

    @Provides ShotModelMapper provideShotModelMapper(){ return new ShotModelMapper();}

    @Provides MatchModelMapper provideMatchModelMapper(Application application){ return new MatchModelMapper(application);}

    @Provides UserWatchingModelMapper provideUserWatchingModelMapper(Application application){ return new UserWatchingModelMapper(application);}

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

    @Provides MatchMapper provideMatchMapper(){ return new MatchMapper();}

    @Provides WatchMapper provideWatchMapper(){ return new WatchMapper();}

    @Provides TeamMapper provideTeamMapper(){ return new TeamMapper();}

    @Provides WatchingRequestModelMapper provideWatchingRequestModelMapper(Application application){
        return new WatchingRequestModelMapper(application);
    }

}
