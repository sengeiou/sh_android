package com.shootr.android.data.service;

import com.shootr.android.domain.service.user.CheckinGateway;
import dagger.Module;
import dagger.Provides;

@Module(
  complete = false,
  library = true
)
public class ServiceModule {

    @Provides CheckinGateway provideCheckinGateway(DataserviceCheckinGateway dataserviceCheckinGateway) {
        return dataserviceCheckinGateway;
    }
}
