package com.shootr.android.data.service;

import com.shootr.android.domain.service.user.CheckinGateway;
import com.shootr.android.domain.service.user.CreateAccountGateway;
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

    @Provides CreateAccountGateway provideCreateAccountGateway(DataserviceCreateAccountGateway dataserviceCreateAccountGateway) {
        return dataserviceCreateAccountGateway;
    }
}
