package com.shootr.android.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import com.shootr.android.service.dataservice.ShootrDataService;
import com.shootr.android.service.dataservice.DataServiceModule;
import com.shootr.android.task.jobs.loginregister.LoginUserJob;
import javax.inject.Singleton;

@Module(
        injects = {
                LoginUserJob.class,
        },
        includes = DataServiceModule.class,
        complete = false
)
public final class ApiModule {

    public static final String PRODUCTION_API_URL = "http://tst.shootermessenger.com/data-services/rest/generic";

    @Provides @Singleton ShootrService provideShootrService(ShootrDataService dataService) {
        return dataService;
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