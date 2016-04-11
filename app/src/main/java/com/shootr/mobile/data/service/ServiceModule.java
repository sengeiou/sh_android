package com.shootr.mobile.data.service;

import com.shootr.mobile.domain.service.shot.ShotGateway;
import com.shootr.mobile.domain.service.user.ChangePasswordGateway;
import com.shootr.mobile.domain.service.user.ConfirmEmailGateway;
import com.shootr.mobile.domain.service.user.CreateAccountGateway;
import com.shootr.mobile.domain.service.user.LoginGateway;
import com.shootr.mobile.domain.service.user.ResetPasswordEmailGateway;
import com.shootr.mobile.domain.service.user.ResetPasswordGateway;
import dagger.Module;
import dagger.Provides;

@Module(
  complete = false,
  library = true) public class ServiceModule {

    @Provides ChangePasswordGateway provideChangePasswordGateway(
      ServiceChangePasswordGateway serviceChangePasswordGateway) {
        return serviceChangePasswordGateway;
    }

    @Provides CreateAccountGateway provideCreateAccountGateway(
      ServiceCreateAccountGateway serviceCreateAccountGateway) {
        return serviceCreateAccountGateway;
    }

    @Provides LoginGateway provideCheckinGateway(ServiceLoginGateway serviceLoginGateway) {
        return serviceLoginGateway;
    }

    @Provides ShotGateway provideShotGateway(SpecialserviceShotGateway specialserviceShotGateway) {
        return specialserviceShotGateway;
    }

    @Provides ConfirmEmailGateway provideConfirmEmailGateway(ServiceConfirmEmailGateway serviceConfirmEmailGateway) {
        return serviceConfirmEmailGateway;
    }

    @Provides ResetPasswordGateway provideForgotPasswordGateway(
      ServiceResetPasswordGateway serviceForgotPasswordGateway) {
        return serviceForgotPasswordGateway;
    }

    @Provides ResetPasswordEmailGateway provideResetPasswordEmailGateway(
      ServiceResetPasswordEmailGateway serviceForgotPasswordEmailGateway) {
        return serviceForgotPasswordEmailGateway;
    }
}
