package com.shootr.android.data.service;

import com.shootr.android.domain.service.shot.ShotGateway;
import com.shootr.android.domain.service.user.ChangePasswordGateway;
import com.shootr.android.domain.service.user.CheckinGateway;
import com.shootr.android.domain.service.user.ConfirmEmailGateway;
import com.shootr.android.domain.service.user.CreateAccountGateway;
import com.shootr.android.domain.service.user.LoginGateway;
import com.shootr.android.domain.service.user.ResetPasswordEmailGateway;
import com.shootr.android.domain.service.user.ResetPasswordGateway;
import dagger.Module;
import dagger.Provides;

@Module(
  complete = false,
  library = true
)
public class ServiceModule {

    @Provides CheckinGateway provideCheckinGateway(ServiceCheckinGateway serviceCheckinGateway) {
        return serviceCheckinGateway;
    }

    @Provides ChangePasswordGateway provideChangePasswordGateway(DataserviceChangePasswordGateway dataserviceChangePasswordGateway) {
        return dataserviceChangePasswordGateway;
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

    @Provides ConfirmEmailGateway provideConfirmEmailGateway(DataserviceConfirmEmailGateway dataserviceConfirmEmailGateway) {
        return dataserviceConfirmEmailGateway;
    }

    @Provides
    ResetPasswordGateway provideForgotPasswordGateway(DataserviceResetPasswordGateway dataserviceForgotPasswordGateway){
        return dataserviceForgotPasswordGateway;
    }

    @Provides ResetPasswordEmailGateway provideResetPasswordEmailGateway(DataserviceResetPasswordEmailGateway dataserviceForgotPasswordEmailGateway){
        return dataserviceForgotPasswordEmailGateway;
    }
}
