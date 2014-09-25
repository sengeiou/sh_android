package gm.mobi.android.service.dataservice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.service.dataservice.dto.TimelineDtoFactory;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.service.dataservice.dto.UtilityDtoFactory;

@Module(
        injects = {
                BagdadDataService.class
        },
        library = true,
        complete = false
)
public class DataServiceModule {

    @Provides @Singleton UtilityDtoFactory utilityDtoFactory() {
        return new UtilityDtoFactory();
    }

    @Provides @Singleton UserDtoFactory provideUserDtoFactory(UtilityDtoFactory utilityDtoFactory) {
        return new UserDtoFactory(utilityDtoFactory);
    }

    @Provides @Singleton TimelineDtoFactory provideTimelineDtoFactory(UtilityDtoFactory utilityDtoFactory) {
        return new TimelineDtoFactory(utilityDtoFactory);
    }
}
