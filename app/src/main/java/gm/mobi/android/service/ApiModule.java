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
                return "http://tst.shootermessenger.com/data-services/rest/generic";
            }

            @Override
            public String getName() {
                return "Test";
            }
        };
    }
}
