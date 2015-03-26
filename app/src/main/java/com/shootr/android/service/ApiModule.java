package com.shootr.android.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.domain.repository.PhotoService;
import com.shootr.android.service.dataservice.ShootrPhotoService;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.service.dataservice.ShootrDataService;
import com.shootr.android.service.dataservice.DataServiceModule;
import com.shootr.android.task.jobs.loginregister.LoginUserJob;
import javax.inject.Singleton;

@Module(
        injects = {
                LoginUserJob.class,
                ShootrPhotoService.class,
                PhotoService.class,
        },
        includes = DataServiceModule.class,
        complete = false,
  library = true
)
public final class ApiModule {

    public static final String PRODUCTION_API_URL = "http://tst.dataservices.shootr.com/data-services/rest/generic/";

    @Provides @Singleton ShootrService provideShootrService(ShootrDataService dataService) {
        return dataService;
    }

    @Provides @Singleton PhotoService providePhotoService(ShootrPhotoService photoService) {
        return photoService;
    }

    @Provides @Singleton ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides @Singleton Endpoint provideEndpoint() {
        return new Endpoint() {
            @Override
            public String getUrl() {
                return PRODUCTION_API_URL;
            }

            @Override
            public String getName() {
                return "Production";
            }
        };
    }
}
