package com.shootr.android.service.dataservice;

import android.app.Application;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.ForgotPasswordMapper;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.service.dataservice.dto.ShotDtoFactory;
import com.shootr.android.service.dataservice.dto.TimelineDtoFactory;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.service.dataservice.dto.UtilityDtoFactory;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                ShootrDataService.class
        },
        library = true,
        complete = false
)
public class DataServiceModule {

    @Provides @Singleton UtilityDtoFactory utilityDtoFactory(Application application) {
        return new UtilityDtoFactory(application);
    }

    @Provides @Singleton UserDtoFactory provideUserDtoFactory(UtilityDtoFactory utilityDtoFactory, UserMapper userMapper, FollowMapper followMapper, ForgotPasswordMapper forgotPasswordMapper) {
        return new UserDtoFactory(utilityDtoFactory, userMapper, followMapper, forgotPasswordMapper);
    }

    @Provides @Singleton TimelineDtoFactory provideTimelineDtoFactory(UtilityDtoFactory utilityDtoFactory, ShotEntityMapper shotEntityMapper) {
        return new TimelineDtoFactory(utilityDtoFactory, shotEntityMapper);
    }

    @Provides @Singleton ShotDtoFactory provideShotDtoFactory(UtilityDtoFactory utilityDtoFactory, ShotEntityMapper shotEntityMapper) {
        return new ShotDtoFactory(utilityDtoFactory, shotEntityMapper);
    }
}
