package gm.mobi.android.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gm.mobi.android.data.ApiEndpoint;
import gm.mobi.android.data.IsMockMode;
import gm.mobi.android.data.prefs.StringPreference;
import gm.mobi.android.service.dataservice.BagdadDataService;
import hugo.weaving.DebugLog;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class DebugApiModule {

    @DebugLog
    @Provides @Singleton Endpoint provideEndpoint(@ApiEndpoint StringPreference apiEndpoint) {
        return Endpoints.newFixedEndpoint(apiEndpoint.get());
    }

    @Provides @Singleton BagdadMockService provideMockBagdadService() {
        return new BagdadMockService();
    }

    @Provides @Singleton BagdadService provideBagdadService(OkHttpClient client, Endpoint endpoint, ObjectMapper mapper, @IsMockMode boolean isMockMode) {
        if (isMockMode) {
            return new BagdadMockService();
        } else {
            return new BagdadDataService(client, endpoint, mapper);
        }
    }
}
