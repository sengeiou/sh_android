package gm.mobi.android.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.service.dataservice.BagdadDataService;
import gm.mobi.android.task.jobs.LoginUserJob;

@Module(
        injects = {
                LoginUserJob.class,
        },
        complete = false
)
public final class ApiModule {

    public static final String PRODUCTION_API_URL = "http://tst.shootermessenger.com/data-services/rest/generic";

    @Provides @Singleton BagdadService provideBagdadService(OkHttpClient client, Endpoint endpoint, ObjectMapper mapper) {
        return new BagdadDataService(client, endpoint, mapper);
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
