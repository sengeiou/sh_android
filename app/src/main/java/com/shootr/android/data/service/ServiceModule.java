package com.shootr.android.data.service;

import com.shootr.android.domain.service.shot.ShotGateway;
import com.shootr.android.domain.service.user.CheckinGateway;
import com.shootr.android.domain.service.user.CreateAccountGateway;
import com.shootr.android.domain.service.user.ResetPasswordGateway;
import com.shootr.android.domain.service.user.LoginGateway;
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

    @Provides LoginGateway provideCheckinGateway(DataserviceLoginGateway dataserviceLoginGateway) {
        return dataserviceLoginGateway;
    }

    @Provides ShotGateway provideShotGateway(SpecialserviceShotGateway specialserviceShotGateway) {
        return specialserviceShotGateway;
    }

    @Provides
    ResetPasswordGateway provideForgotPasswordGateway(DataserviceResetPasswordGateway dataserviceForgotPasswordGateway){
        return dataserviceForgotPasswordGateway;
    }
}
